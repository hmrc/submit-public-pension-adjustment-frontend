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

import config.{FrontendAppConfig, Service}
import connectors.ConnectorFailureLogger.FromResultToConnectorFailureLogger
import models.Done
import play.api.{Configuration, Logging}
import play.api.http.Status.{NOT_FOUND, NO_CONTENT, OK}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculateBackendConnector @Inject() (
  config: Configuration,
  httpClient: HttpClientV2,
  frontendAppConfig: FrontendAppConfig
)(implicit
  ec: ExecutionContext
) extends Logging {

  private val baseUrlCalcBE        = config.get[Service]("microservice.services.calculate-public-pension-adjustment")
  private val userAnswersUrlCalcBE = url"$baseUrlCalcBE/calculate-public-pension-adjustment/user-answers"

  private val submissionsUrlCalcBE = url"$baseUrlCalcBE/calculate-public-pension-adjustment/submission"

  def clearUserAnswersCalcBE()(implicit hc: HeaderCarrier): Future[Done] =
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

  def clearSubmissionCalcBE()(implicit hc: HeaderCarrier): Future[Done] =
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

  def sendFlagResetSignal(submissionUniqueId: String)(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .get(
        url"${frontendAppConfig.cppaBaseUrl}/calculate-public-pension-adjustment/submission-status-update/$submissionUniqueId"
      )
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(Done)
          case _  =>
            logger.error(
              s"Unexpected response from /submit-public-pension-adjustment/submission-reset-signal with status : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from submit-public-pension-adjustment/submission-reset-signal",
                response.status
              )
            )
        }
      }

  def updateCalcBEWithUserAnswers(uniqueId: String)(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .get(
        url"${frontendAppConfig.cppaBaseUrl}/calculate-public-pension-adjustment/check-and-retrieve-calc-user-answers/$uniqueId"
      )
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case OK         =>
            Future.successful(Done)
          case NO_CONTENT => Future.successful(Done)
          case _          =>
            logger.error(
              s"Unexpected response from /calculate-public-pension-adjustment/check-and-retrieve-calc-user-answers with status : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from calculate-public-pension-adjustment/check-and-retrieve-calc-user-answers",
                response.status
              )
            )
        }
      }
}
