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

package bars

import bars.barsmodel.response._
import base.SpecBase
import models.BankDetails
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

class PsprBarsServiceSpec extends SpecBase with MockitoSugar {

  "PsprBarsService" - {
    "should return Left(BarsError) when underlying BarsService returns an error response" in {
      val headerCarrier = new HeaderCarrier()
      val barsService   = mock[BarsService]

      val errorResponse = BarsErrorResponse("code", "desc")
      val barsResponse  = Left(SortCodeOnDenyListErrorResponse(SortCodeOnDenyList(errorResponse)))

      when(barsService.verifyBankDetails(any(), any())(any())).thenReturn(Future.successful(barsResponse))
      val psprBarsService = new PsprBarsService(barsService)

      val bankAccountDetails = BankDetails("accountName", "sortCode", "accountNumber")
      val response           = psprBarsService.verifyBankDetails(bankAccountDetails)(headerCarrier, global)
      response.futureValue shouldBe barsResponse
    }
  }

  "when verifying personal details" - {
    "should return Valid when underlying BarsService returns a successful VerifyResponse" in {
      val headerCarrier = new HeaderCarrier()
      val barsService   = mock[BarsService]

      when(barsService.verifyPersonal(any(), any())(any()))
        .thenReturn(Future.successful(VerifyResponse(aBarsVerifyResponse)))
      val psprBarsService = new PsprBarsService(barsService)

      val bankAccountDetails = BankDetails("accountName", "sortCode", "accountNumber")
      val response           = psprBarsService.verifyPersonal(bankAccountDetails)(headerCarrier, global)
      response.futureValue shouldBe bars.PsprBarsService.Valid
    }
  }

  private def aBarsVerifyResponse =
    BarsVerifyResponse(
      accountNumberIsWellFormatted = BarsAssessmentType.Yes,
      accountExists = BarsAssessmentType.Yes,
      nameMatches = BarsAssessmentType.Yes,
      nonStandardAccountDetailsRequiredForBacs = BarsAssessmentType.Yes,
      sortCodeIsPresentOnEISCD = BarsAssessmentType.Yes,
      sortCodeSupportsDirectDebit = BarsAssessmentType.Yes,
      sortCodeSupportsDirectCredit = BarsAssessmentType.Yes,
      accountName = Some("John Smith"),
      sortCodeBankName = Some("111111"),
      iban = None
    )
}
