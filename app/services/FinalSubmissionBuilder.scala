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

import models.UserAnswers
import models.calculation.inputs.CalculationInputs
import models.calculation.response.CalculationResponse
import models.finalsubmission._

object FinalSubmissionBuilder {

  // TODO Implementation pending.
  def buildFinalSubmission(
    caseNumber: String,
    calculationInputs: CalculationInputs,
    calculation: Option[CalculationResponse],
    submissionUserAnswers: UserAnswers
  ): FinalSubmission = {

    val submissionInputs = SubmissionInputs(
      administrativeDetails = buildAdministrativeDetails(submissionUserAnswers),
      paymentElections = buildPaymentElections(submissionUserAnswers),
      calculationInputSchemeIdentifiers = buildCalculationInputSchemeIdentifiers(submissionUserAnswers),
      schemeTaxRelief = buildSchemeTaxRelief(submissionUserAnswers),
      bankAccountDetails = buildBankAccountDetails(submissionUserAnswers),
      declarations = buildDeclarations(submissionUserAnswers)
    )

    FinalSubmission(calculationInputs, calculation, submissionInputs)
  }

  private def buildAdministrativeDetails(submissionUserAnswers: UserAnswers): AdministrativeDetails =
    AdministrativeDetails(
      ClaimantDetails(
        PersonalDetails(
          fullName = "fullName",
          alternateName = None,
          dateOfBirth = None,
          address = None,
          internationalAddress = None,
          contactPhoneNumber = None,
          pensionSchemeMemberAddress = None,
          pensionSchemeMemberInternationalAddress = None
        ),
        TaxIdentifiers(None, None, None)
      ),
      None
    )

  private def buildPaymentElections(submissionUserAnswers: UserAnswers): List[PaymentElection] = List.empty

  private def buildCalculationInputSchemeIdentifiers(
    submissionUserAnswers: UserAnswers
  ): List[IndividualSchemeIdentifier] = List.empty

  private def buildSchemeTaxRelief(submissionUserAnswers: UserAnswers): Option[SchemeTaxRelief] = None

  private def buildBankAccountDetails(submissionUserAnswers: UserAnswers): Option[BankAccountDetails] = None

  private def buildDeclarations(submissionUserAnswers: UserAnswers): Declarations = Declarations(
    compensation = true,
    tax = true,
    contactDetails = true,
    powerOfAttorney = None,
    claimOnBehalfOfDeceased = None
  )
}
