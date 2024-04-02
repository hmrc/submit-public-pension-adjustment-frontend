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

import config.Service
import connectors.ConnectorFailureLogger.FromResultToConnectorFailureLogger
import models.Done
import play.api.Configuration
import play.api.http.Status.NO_CONTENT
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculateBackendConnector @Inject() (config: Configuration, httpClient: HttpClientV2)(implicit
  ec: ExecutionContext
) {

  private val baseUrlCalcBE        = config.get[Service]("microservice.services.calculate-public-pension-adjustment")
  private val userAnswersUrlCalcBE = url"$baseUrlCalcBE/calculate-public-pension-adjustment/user-answers"

  private val submissionsUrlCalcBE = url"$baseUrlCalcBE/calculate-public-pension-adjustment/submission"

  def clearCalcUserAnswersBE()(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .delete(userAnswersUrlCalcBE)
      .execute[HttpResponse]
      .logFailureReason(connectorName = "`UserAnswersConnector` on clearCalcBE")
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(Done)
        } else {
          Future.failed(UpstreamErrorResponse("", response.status))
        }
      }

  def clearCalcSubmissionBE()(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .delete(submissionsUrlCalcBE)
      .execute[HttpResponse]
      .logFailureReason(connectorName = "SubmissionsConnector on clearCalcBE")
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(Done)
        } else {
          Future.failed(UpstreamErrorResponse("", response.status))
        }
      }
}
