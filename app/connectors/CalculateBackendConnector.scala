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

package connectors

import com.google.inject.Inject
import config.FrontendAppConfig
import models.UniqueId
import models.submission.RetrieveSubmissionResponse
import play.api.Logging
import play.api.http.Status._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class CalculateBackendConnector @Inject() (
  config: FrontendAppConfig,
  httpClient2: HttpClientV2
)(implicit
  ec: ExecutionContext
) extends Logging {

  def retrieveSubmission(
    submissionUniqueId: UniqueId
  )(implicit hc: HeaderCarrier): Future[RetrieveSubmissionResponse] =
    httpClient2
      .get(url"${config.cppaBaseUrl}/calculate-public-pension-adjustment/submission/${submissionUniqueId.value}")
      .execute
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[RetrieveSubmissionResponse])
          case _  =>
            logger.error(
              s"Unexpected response from /calculate-public-pension-adjustment/submission/${submissionUniqueId.value} with status : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from /calculate-public-pension-adjustment/submission/submissionUniqueId",
                response.status
              )
            )
        }
      }

}
