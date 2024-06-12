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

import base.SpecBase
import connectors.SubmitBackendConnector
import models.{PensionSchemeMemberInternationalAddress, SchemeCreditConsent, UkAddress}
import models.finalsubmission.{BankAccountDetails, Declarations, FinalSubmissionResponse, PersonalDetails, TaxIdentifiers}
import org.mockito.{ArgumentMatchers, MockitoSugar}
import pages.TestData
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.Future

class SubmissionServiceSpec extends SpecBase with MockitoSugar {

  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()

  private val mockSubmitBackendConnector = mock[SubmitBackendConnector]

  private val service = new SubmissionService(mockSubmitBackendConnector)

  "SubmissionService" - {

    "sendFinalSubmission should return FinalSubmissionResponse for valid inputs" in {

      val finalSubmissionResponse = FinalSubmissionResponse("P3Z87CEQ1OWL")

      when(
        mockSubmitBackendConnector.sendFinalSubmission(ArgumentMatchers.eq(TestData.finalSubmission1))(
          ArgumentMatchers.eq(headerCarrier)
        )
      )
        .thenReturn(Future.successful(finalSubmissionResponse))

      val result = service.sendFinalSubmission(
        TestData.authRetrievals,
        TestData.calculationInputs,
        TestData.calculation,
        TestData.userAnswers1
      )(implicitly(headerCarrier))

      result.futureValue mustBe finalSubmissionResponse
    }

    "buildFinalSubmission with claimOnBehalf = false should return FinalSubmission for valid inputs" in {

      val result = service.buildFinalSubmission(
        TestData.authRetrievals,
        TestData.calculationInputs,
        TestData.calculation,
        TestData.userAnswers1
      )

      result mustBe TestData.finalSubmission1
    }

    "buildFinalSubmission with claimOnBehalf = true should return FinalSubmission for valid inputs" in {

      val result = service.buildFinalSubmission(
        TestData.authRetrievals,
        TestData.calculationInputs,
        TestData.calculation,
        TestData.userAnswers2
      )

      result mustBe TestData.finalSubmission2
    }

    "buildSubmissionInputs should return SubmissionInputs for valid inputs" in {

      val result = service.buildSubmissionInputs(
        TestData.authRetrievals,
        TestData.calculationInputs,
        TestData.calculation,
        TestData.userAnswers1
      )

      result mustBe TestData.finalSubmission1.submissionInputs
    }

    "buildPersonalDetails with claimOnBehalf = false should return PersonalDetails for valid inputs" in {

      val result = service.buildPersonalDetails(
        false,
        TestData.authRetrievals,
        TestData.userAnswers1
      )

      result mustBe PersonalDetails(
        "Test User",
        None,
        Some(LocalDate.of(1968, 11, 30)),
        Some(UkAddress("Test Address line 1", None, "Test city", Some("Test county"), "AB1 2CD")),
        None,
        None,
        None,
        Some("01632 960 001")
      )
    }

    "buildPersonalDetails with claimOnBehalf = true should return PersonalDetails for valid inputs" in {

      val result = service.buildPersonalDetails(
        true,
        TestData.authRetrievals,
        TestData.userAnswers2
      )

      result mustBe PersonalDetails(
        "Test SchemeUser",
        Some("Duplicate Name"),
        Some(LocalDate.parse("1962-10-24")),
        None,
        None,
        None,
        Some(
          PensionSchemeMemberInternationalAddress(
            "Test Address line 1",
            Some("Test Address line 2"),
            "Test city",
            None,
            Some("NP4 9KL"),
            "Spain"
          )
        ),
        Some("01632 960 001")
      )
    }

    "buildTaxIdentifiers with claimOnBehalf = false should return TaxIdentifiers for valid inputs" in {

      val result = service.buildTaxIdentifiers(
        false,
        TestData.authRetrievals,
        TestData.userAnswers1
      )

      result mustBe TaxIdentifiers(Some("AA000000A"), None, Some("1234567890"))
    }

    "buildTaxIdentifiers with claimOnBehalf = true should return TaxIdentifiers for valid inputs" in {

      val result = service.buildTaxIdentifiers(
        true,
        TestData.authRetrievals,
        TestData.userAnswers2
      )

      result mustBe TaxIdentifiers(Some("AB123456C"), None, Some("1234567890"))
    }

    "buildBankAccountDetails should return BankAccountDetails for valid inputs" in {

      val result = service.buildBankAccountDetails(TestData.userAnswers3)

      result mustBe Some(BankAccountDetails("TestFName TestLName", "012345", "12345678"))
    }

    "buildDeclarations with StatusOfUserPage having no PowerOfAttorney, Deputyship and SchemeCreditConsent should return valid Declarations" in {

      val result = service.buildDeclarations(TestData.userAnswers1)

      result mustBe Declarations(
        compensation = true,
        tax = true,
        contactDetails = true,
        powerOfAttorney = None,
        claimOnBehalfOfDeceased = None,
        schemeCreditConsent = None
      )
    }

    "buildDeclarations with StatusOfUserPage having PowerOfAttorney and schemeCreditConsent should return valid Declarations" in {

      val result = service.buildDeclarations(TestData.userAnswers2)

      result mustBe Declarations(
        compensation = true,
        tax = true,
        contactDetails = true,
        powerOfAttorney = Some(true),
        claimOnBehalfOfDeceased = None,
        schemeCreditConsent = Some(SchemeCreditConsent.Yes)
      )
    }

    "buildDeclarations with StatusOfUserPage having Deputyship should return valid Declarations" in {

      val result = service.buildDeclarations(TestData.userAnswers3)

      result mustBe Declarations(
        compensation = true,
        tax = true,
        contactDetails = true,
        powerOfAttorney = None,
        claimOnBehalfOfDeceased = Some(true),
        schemeCreditConsent = None
      )
    }

  }

}
