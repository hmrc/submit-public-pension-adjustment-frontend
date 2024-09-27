/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import connectors.SubmitBackendConnector
import models.StatusOfUser.Deputyship
import models.WhenWillYouAskPensionSchemeToPay._
import models.WhoWillPay.{PensionScheme, You}
import models.calculation.inputs.CalculationInputs
import models.calculation.response.{CalculationResponse, Period}
import models.finalsubmission._
import models.{PSTR, SchemeCreditConsent, StatusOfUser, UserAnswers}
import pages._
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendHeaderCarrierProvider

import javax.inject.Inject
import scala.concurrent.Future

class SubmissionService @Inject() (submitBackendConnector: SubmitBackendConnector)
    extends FrontendHeaderCarrierProvider
    with Logging {

  def sendFinalSubmission(
    authRetrievals: AuthRetrievals,
    calculationInputs: CalculationInputs,
    calculation: Option[CalculationResponse],
    userAnswers: UserAnswers
  )(implicit hc: HeaderCarrier): Future[FinalSubmissionResponse] =
    submitBackendConnector.sendFinalSubmission(
      buildFinalSubmission(authRetrievals, calculationInputs, calculation, userAnswers)
    )

  def buildFinalSubmission(
    authRetrievals: AuthRetrievals,
    calculationInputs: CalculationInputs,
    calculation: Option[CalculationResponse],
    userAnswers: UserAnswers
  ): FinalSubmission =
    FinalSubmission(
      calculationInputs,
      calculation,
      buildSubmissionInputs(authRetrievals, calculationInputs, calculation, userAnswers)
    )

  def buildSubmissionInputs(
    authRetrievals: AuthRetrievals,
    calculationInputs: CalculationInputs,
    calculation: Option[CalculationResponse],
    userAnswers: UserAnswers
  ): SubmissionInputs =
    SubmissionInputs(
      buildAdministrativeDetails(authRetrievals, userAnswers),
      buildPaymentElection(userAnswers, calculation),
      buildCalculationInputSchemeIdentifiers(userAnswers, calculationInputs),
      buildSchemeTaxRelief(userAnswers),
      buildBankAccountDetails(userAnswers),
      buildDeclarations(userAnswers)
    )

  def buildPersonalDetails(
    flagClaimOnBehalf: Boolean,
    authRetrievals: AuthRetrievals,
    userAnswers: UserAnswers
  ): PersonalDetails = {

    val fullName =
      if (flagClaimOnBehalf)
        userAnswers.get(PensionSchemeMemberNamePage).getOrElse("")
      else
        authRetrievals.name

    val alternateName = userAnswers.get(EnterAlternativeNamePage)

    val dateOfBirth =
      if (flagClaimOnBehalf)
        userAnswers.get(PensionSchemeMemberDOBPage)
      else
        authRetrievals.dob

    val address = userAnswers.get(UkAddressPage)

    val internationalAddress = userAnswers.get(InternationalAddressPage)

    val pensionSchemeMemberAddress = userAnswers.get(PensionSchemeMemberUKAddressPage)

    val pensionSchemeMemberInternationalAddress = userAnswers.get(PensionSchemeMemberInternationalAddressPage)

    val contactPhoneNumber = userAnswers.get(ContactNumberPage)

    if (flagClaimOnBehalf)
      PersonalDetails(
        fullName,
        alternateName,
        dateOfBirth,
        None,
        None,
        pensionSchemeMemberAddress,
        pensionSchemeMemberInternationalAddress,
        contactPhoneNumber
      )
    else
      PersonalDetails(
        fullName,
        alternateName,
        dateOfBirth,
        address,
        internationalAddress,
        None,
        None,
        contactPhoneNumber
      )

  }

  def buildTaxIdentifiers(
    flagClaimOnBehalf: Boolean,
    authRetrievals: AuthRetrievals,
    userAnswers: UserAnswers
  ): TaxIdentifiers = {

    val nino: Option[String] =
      if (flagClaimOnBehalf) {
        userAnswers.get(PensionSchemeMemberNinoPage).map(_.nino)
      } else
        Some(authRetrievals.userId)

    val utr: Option[String] =
      if (flagClaimOnBehalf) {
        userAnswers.get(PensionSchemeMemberTaxReferencePage)
      } else
        authRetrievals.saUtr

    TaxIdentifiers(nino, None, utr)
  }

  def buildClaimantDetails(authRetrievals: AuthRetrievals, userAnswers: UserAnswers) =
    ClaimantDetails(
      buildPersonalDetails(false, authRetrievals, userAnswers),
      buildTaxIdentifiers(false, authRetrievals, userAnswers)
    )

  def buildOnBehalfOfMember(authRetrievals: AuthRetrievals, userAnswers: UserAnswers): OnBehalfOfMember = {
    val memberType: OnBehalfOfMemberType =
      userAnswers.get(StatusOfUserPage) match {
        case Some(Deputyship) => OnBehalfOfMemberType.Deceased
        case _                => OnBehalfOfMemberType.PowerOfAttorney
      }

    OnBehalfOfMember(
      buildPersonalDetails(true, authRetrievals, userAnswers),
      buildTaxIdentifiers(true, authRetrievals, userAnswers),
      userAnswers.get(MemberDateOfDeathPage),
      memberType
    )
  }

  def buildAdministrativeDetails(
    authRetrievals: AuthRetrievals,
    userAnswers: UserAnswers
  ): AdministrativeDetails =
    userAnswers.get(ClaimOnBehalfPage) match {
      case Some(true) =>
        AdministrativeDetails(
          buildClaimantDetails(authRetrievals, userAnswers),
          Some(buildOnBehalfOfMember(authRetrievals, userAnswers))
        )
      case _          =>
        AdministrativeDetails(buildClaimantDetails(authRetrievals, userAnswers), None)
    }

  def buildPaymentElection(
    userAnswers: UserAnswers,
    calculation: Option[CalculationResponse]
  ): List[PaymentElection] = {

    val inDatesYears = calculation
      .map {
        _.inDates.filter(_.debit != 0).map(v => (v.period, v.debit))
      }
      .getOrElse(Nil)

    inDatesYears.map { v =>
      PaymentElection(
        v._1.toCalculationInputsPeriod,
        buildPersonalCharge(v._1, v._2, userAnswers),
        buildSchemeCharge(v._1, v._2, userAnswers)
      )
    }
  }

  def buildPersonalCharge(
    period: Period,
    debit: Int,
    userAnswers: UserAnswers
  ): Option[PersonalCharge] =
    userAnswers.get(WhoWillPayPage(period.toCorePeriod)) flatMap {
      case You           => Some(PersonalCharge(debit))
      case PensionScheme => None
    }

  def buildSchemeCharge(
    period: Period,
    debit: Int,
    userAnswers: UserAnswers
  ): Option[SchemeCharge] =
    userAnswers.get(WhoWillPayPage(period.toCorePeriod)) flatMap {
      case You           => None
      case PensionScheme =>
        val paymentElectionDate = userAnswers.get(WhenDidYouAskPensionSchemeToPayPage(period.toCorePeriod))

        Some(
          SchemeCharge(
            debit,
            buildSchemeDetails(userAnswers, period).getOrElse(SchemeDetails("", PSTR(""))),
            paymentElectionDate
          )
        )
    }

  def buildSchemeDetails(userAnswers: UserAnswers, period: Period): Option[SchemeDetails] =
    userAnswers.get(WhichPensionSchemeWillPayPage(period.toCorePeriod)) match {
      case Some("Private pension scheme") =>
        userAnswers
          .get(PensionSchemeDetailsPage(period.toCorePeriod))
          .map(v => SchemeDetails(v.pensionSchemeName, PSTR(v.pensionSchemeTaxReference)))
      case Some(v)                        =>
        val s = v.split("/").toList
        Some(SchemeDetails(s.head.trim, PSTR(s.last.trim)))
      case None                           =>
        None
    }

  def buildBankAccountDetails(userAnswers: UserAnswers): Option[BankAccountDetails] =
    userAnswers.get(BankDetailsPage).map(v => BankAccountDetails(v.accountName, v.sortCode, v.accountNumber))

  def buildDeclarations(userAnswers: UserAnswers): Declarations =
    userAnswers.get(StatusOfUserPage) match {
      case Some(StatusOfUser.PowerOfAttorney) =>
        Declarations(
          compensation = true,
          tax = true,
          contactDetails = true,
          powerOfAttorney = Some(true),
          claimOnBehalfOfDeceased = None,
          legalPersonalRepresentative = None,
          schemeCreditConsent = userAnswers.get(SchemeCreditConsent)
        )

      case Some(StatusOfUser.Deputyship) =>
        Declarations(
          compensation = true,
          tax = true,
          contactDetails = true,
          powerOfAttorney = None,
          claimOnBehalfOfDeceased = Some(true),
          legalPersonalRepresentative = None,
          schemeCreditConsent = userAnswers.get(SchemeCreditConsent)
        )

      case Some(StatusOfUser.LegalPersonalRepresentative) =>
        Declarations(
          compensation = true,
          tax = true,
          contactDetails = true,
          powerOfAttorney = None,
          claimOnBehalfOfDeceased = None,
          legalPersonalRepresentative = Some(true),
          schemeCreditConsent = userAnswers.get(SchemeCreditConsent)
        )

      case _ =>
        Declarations(
          compensation = true,
          tax = true,
          contactDetails = true,
          powerOfAttorney = None,
          claimOnBehalfOfDeceased = None,
          legalPersonalRepresentative = None,
          schemeCreditConsent = userAnswers.get(SchemeCreditConsent)
        )
    }

  def buildCalculationInputSchemeIdentifiers(
    userAnswers: UserAnswers,
    calculationInputs: CalculationInputs
  ): List[IndividualSchemeIdentifier] =
    SchemeService
      .allPensionSchemeDetails(calculationInputs)
      .map { ps =>
        IndividualSchemeIdentifier(
          SchemeDetails(ps.pensionSchemeName, PSTR(ps.pensionSchemeTaxReference)),
          userAnswers.get(LegacyPensionSchemeReferencePage(PSTR(ps.pensionSchemeTaxReference), ps.pensionSchemeName)),
          userAnswers.get(ReformPensionSchemeReferencePage(PSTR(ps.pensionSchemeTaxReference), ps.pensionSchemeName))
        )
      }
      .toList

  def buildSchemeTaxRelief(userAnswers: UserAnswers): Option[SchemeTaxRelief] =
    userAnswers.get(ClaimingHigherOrAdditionalTaxRateReliefPage) flatMap {
      case true =>
        val amount                     = userAnswers.get(HowMuchTaxReliefPage).map(_.toInt)
        val individualSchemeIdentifier =
          userAnswers.get(WhichPensionSchemeWillPayTaxReliefPage).map { v =>
            val ps              = v.split("/").toList
            val schemeDetails   = SchemeDetails(ps.head.trim, PSTR(ps.last.trim))
            val legacyReference =
              userAnswers.get(LegacyPensionSchemeReferencePage(schemeDetails.pstr, schemeDetails.schemeName))
            val reformReference =
              userAnswers.get(ReformPensionSchemeReferencePage(schemeDetails.pstr, schemeDetails.schemeName))

            IndividualSchemeIdentifier(schemeDetails, legacyReference, reformReference)
          }

        (amount, individualSchemeIdentifier) match {
          case (Some(a), Some(i)) =>
            Some(SchemeTaxRelief(a, i))
          case _                  =>
            None
        }

      case false =>
        None
    }

}
