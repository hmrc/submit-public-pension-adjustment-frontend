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

package controllers.actions

import base.SpecBase
import models.UserAnswers
import models.calculation.inputs.{CalculationInputs, Resubmission}
import models.requests.{IdentifierRequest, OptionalDataRequest}
import models.submission.Submission
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.FakeRequest
import repositories._
import uk.gov.hmrc.auth.core.retrieve.ItmpName

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with MockitoSugar {

  class Harness(sessionRepository: SessionRepository, submissionRepository: SubmissionRepository)
      extends DataRetrievalActionImpl(sessionRepository, submissionRepository) {
    def callTransform[A](request: IdentifierRequest[A]): Future[OptionalDataRequest[A]] = transform(request)
  }

  "Data Retrieval Action" - {

    "when there is no data in the cache" - {

      "must set userAnswers to 'None' in the request" in {

        val sessionRepository    = mock[SessionRepository]
        val submissionRepository = mock[SubmissionRepository]

        val submission =
          Submission("sessionId", "uniqueId", CalculationInputs(Resubmission(false, None), None, None), None)

        when(sessionRepository.get("id")) thenReturn Future(None)
        when(submissionRepository.getBySessionId(anyString())) thenReturn Future(Some(submission))

        val action = new Harness(sessionRepository, submissionRepository)

        val result = action
          .callTransform(
            IdentifierRequest(
              FakeRequest(),
              "id",
              "nino",
              ItmpName(Some("givenName"), None, Some("familyName")),
              None,
              None
            )
          )
          .futureValue

        result.userAnswers must not be defined
      }
    }

    "when there is data in the cache" - {

      "must build a userAnswers object and add it to the request" in {

        val sessionRepository    = mock[SessionRepository]
        val submissionRepository = mock[SubmissionRepository]

        val submission =
          Submission("sessionId", "uniqueId", CalculationInputs(Resubmission(false, None), None, None), None)

        when(sessionRepository.get("id")) thenReturn Future(Some(UserAnswers("id")))
        when(submissionRepository.getBySessionId(anyString())) thenReturn Future(Some(submission))

        val action = new Harness(sessionRepository, submissionRepository)

        val result =
          action
            .callTransform(
              new IdentifierRequest(
                FakeRequest(),
                "id",
                "nino",
                ItmpName(Some("givenName"), None, Some("familyName")),
                None,
                None
              )
            )
            .futureValue

        result.userAnswers mustBe defined
      }
    }
  }
}
