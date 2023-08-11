/*
 * Copyright 2023 HM Revenue & Customs
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
import models.{InternationalAddress, PSTR, UkAddress, UserAnswers, WhenWillYouAskPensionSchemeToPay}
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
  ): SubmissionInputs = {

    val declarations = Declarations(
      compensation = true,
      tax = true,
      contactDetails = true,
      powerOfAttorney = Some(true),
      claimOnBehalfOfDeceased = None
    )

    SubmissionInputs(
      buildAdministrativeDetails(authRetrievals, userAnswers),
      buildPaymentElection(userAnswers, calculation),
      buildCalculationInputSchemeIdentifiers(userAnswers, calculationInputs),
      buildSchemeTaxRelief(userAnswers),
      buildBankAccountDetails(userAnswers),
      declarations
    )
  }

  def buildPersonalDetails(
    flagClaimOnBehalf: Boolean,
    authRetrievals: AuthRetrievals,
    userAnswers: UserAnswers
  ): PersonalDetails = {

    val fullName =
      if (flagClaimOnBehalf)
        userAnswers.get(PensionSchemeMemberNamePage).getOrElse("")
      else
        authRetrievals.name.getOrElse("")

    val alternateName =
      userAnswers.get(EnterAlternativeNamePage)

    val dateOfBirth =
      if (flagClaimOnBehalf)
        userAnswers.get(PensionSchemeMemberDOBPage)
      else
        authRetrievals.dob

    val address = userAnswers
      .get(PensionSchemeMemberUKAddressPage)
      .map(ua => UkAddress(ua.addressLine1, ua.addressLine2, ua.townOrCity, ua.county, ua.postCode))

    val internationalAddress = userAnswers
      .get(PensionSchemeMemberInternationalAddressPage)
      .map(ia =>
        InternationalAddress(ia.addressLine1, ia.addressLine2, ia.townOrCity, ia.stateOrRegion, ia.postCode, ia.country)
      )

    val contactPhoneNumber = userAnswers.get(ContactNumberPage)

    PersonalDetails(fullName, alternateName, dateOfBirth, address, internationalAddress, contactPhoneNumber)
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
        Some(authRetrievals.nino)

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

        val estimatedPaymentElectionQuarter =
          userAnswers.get(WhenWillYouAskPensionSchemeToPayPage(period.toCorePeriod)) map {
            case OctToDec23  => "1 October 2023 to 31 December 2023"
            case JanToMar24  => "1 January 2024 to 31 March 2024"
            case AprToJune24 => "1 April 2024 to 30 June 2024"
            case JulToSep24  => "1 July 2024 to 30 September 2024"
            case OctToDec24  => "1 October 2024 to 31 December 2024"
            case JanToMar25  => "1 January 2025 to 31 March 2025"
          }

        Some(
          SchemeCharge(
            debit,
            buildSchemeDetails(userAnswers, period).getOrElse(SchemeDetails("", PSTR(""))),
            paymentElectionDate,
            estimatedPaymentElectionQuarter
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
