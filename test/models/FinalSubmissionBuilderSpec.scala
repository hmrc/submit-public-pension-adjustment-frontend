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

package models

import models.finalsubmission.FinalSubmission
import models.submission.RetrieveSubmissionResponse
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import pages._
import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import services.FinalSubmissionBuilder

import java.time.LocalDate
import scala.io.Source

class FinalSubmissionBuilderSpec extends AnyFreeSpec with Matchers with Logging {

  // TODO Implementation pending.
  "Final Submission" - {

    "can be constructed when member with uk address and no alternate name has a charge paid by scheme " +
      "where an election has been made and claiming for additional tax relief" in {

        val submissionResponse: RetrieveSubmissionResponse =
          readRetrieveSubmissionResponse("test/resources/Submission.json")

        val caseNumber = "TBD"

        val submissionUserAnswers     = submissionAnswers(Period._2020)
        val submissionUserAnswersJson = Json.prettyPrint(Json.toJson(submissionUserAnswers))

        logger.info(s"submissionUserAnswersJson : $submissionUserAnswersJson")

        val finalSubmission: FinalSubmission = FinalSubmissionBuilder.buildFinalSubmission(
          caseNumber,
          submissionResponse.calculationInputs,
          submissionResponse.calculation,
          submissionUserAnswers
        )

        finalSubmission.calculation       must be(submissionResponse.calculation)
        finalSubmission.calculationInputs must be(submissionResponse.calculationInputs)

        // finalSubmission.submissionInputs.caseNumber must be(caseNumber)
        // Other assertions once builder is implemented.
      }
  }

  def submissionAnswers(period: Period): UserAnswers =
    UserAnswers("submissionUserAnswersId")
      .set(ClaimOnBehalfPage, false)
      .get
      .set(WhoWillPayPage(period), WhoWillPay.PensionScheme)
      .get
      .set(WhichPensionSchemeWillPayPage(period), "Scheme1_PSTR")
      .get
      .set(WhenDidYouAskPensionSchemeToPayPage(period), LocalDate.of(2020, 6, 30))
      .get
      .set(ContactNumberPage, "contactNumber")
      .get
      .set(UkAddressPage, UkAddress("addressLine1", None, "town", None, "postcode"))
      .get
      .set(LegacyPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1"), "legacyIndividualSchemeRef")
      .get
      .set(ReformPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1"), "reformIndividualSchemeRef")
      .get
      .set(HowMuchTaxReliefPage, BigInt(123))
      .get
      .set(WhichPensionSchemeWillPayTaxReliefPage, "Scheme1 / 00348916RT")
      .get

  def readRetrieveSubmissionResponse(calculationResponseFile: String): RetrieveSubmissionResponse = {
    val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
    val json: JsValue  = Json.parse(source)
    json.as[RetrieveSubmissionResponse]
  }
}
