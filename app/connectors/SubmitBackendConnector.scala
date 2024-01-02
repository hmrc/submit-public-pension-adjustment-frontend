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

package connectors

import com.google.inject.Inject
import config.FrontendAppConfig
import models.finalsubmission.{FinalSubmission, FinalSubmissionResponse}
import play.api.Logging
import play.api.http.Status.OK
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class SubmitBackendConnector @Inject() (
  config: FrontendAppConfig,
  httpClient2: HttpClientV2
)(implicit
  ec: ExecutionContext
) extends Logging {

  def sendFinalSubmission(finalSubmission: FinalSubmission)(implicit
    hc: HeaderCarrier
  ): Future[FinalSubmissionResponse] =
    httpClient2
      .post(url"${config.sppaBaseUrl}/submit-public-pension-adjustment/final-submission")
      .withBody(Json.toJson(finalSubmission))
      .execute
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[FinalSubmissionResponse])
          case _  =>
            logger.error(
              s"Unexpected response from /submit-public-pension-adjustment/final-submission with status : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from submit-public-pension-adjustment/final-submission",
                response.status
              )
            )
        }
      }

}
