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
import models.submission.Submission
import models.{Done, UserAnswers}
import play.api.Configuration
import play.api.http.Status.NO_CONTENT
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionsConnector @Inject() (config: Configuration, httpClient: HttpClientV2)(implicit ec: ExecutionContext) {

  private val baseUrl        = config.get[Service]("microservice.services.submit-public-pension-adjustment")
  private val submissionsUrl = url"$baseUrl/submit-public-pension-adjustment/submissions"
  private val keepAliveUrl   = url"$baseUrl/submit-public-pension-adjustment/submissions/keep-alive"

  private val baseUrlCalc        = config.get[Service]("microservice.services.calculate-public-pension-adjustment")
  private val userAnswersUrlCalc = url"$baseUrlCalc/calculate-public-pension-adjustment/user-answers"

  def get()(implicit hc: HeaderCarrier): Future[Option[Submission]] =
    httpClient
      .get(submissionsUrl)
      .execute[Option[Submission]]
      .logFailureReason(connectorName = "SubmissionsConnector on get")

  def getBySessionId(userId: String)(implicit hc: HeaderCarrier): Future[Option[Submission]] = {
    val submissionsSessionUrl = url"$baseUrl/submit-public-pension-adjustment/submissions/$userId"
    httpClient
      .get(submissionsSessionUrl)
      .execute[Option[Submission]]
      .logFailureReason(connectorName = "SubmissionsConnector on get")
  }
  def insert(answers: UserAnswers)(implicit hc: HeaderCarrier): Future[Done]                 =
    httpClient
      .post(submissionsUrl)
      .withBody(Json.toJson(answers))
      .execute[HttpResponse]
      .logFailureReason(connectorName = "SubmissionsConnector on set")
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(Done)
        } else {
          Future.failed(UpstreamErrorResponse("", response.status))
        }
      }

  def keepAlive()(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .post(keepAliveUrl)
      .execute[HttpResponse]
      .logFailureReason(connectorName = "SubmissionsConnector on keepAlive")
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(Done)
        } else {
          Future.failed(UpstreamErrorResponse("", response.status))
        }
      }

  def clear()(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .delete(submissionsUrl)
      .execute[HttpResponse]
      .logFailureReason(connectorName = "SubmissionsConnector on clear")
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(Done)
        } else {
          Future.failed(UpstreamErrorResponse("", response.status))
        }
      }

  def clearCalc()(implicit hc: HeaderCarrier): Future[Done] =
    httpClient
      .delete(userAnswersUrlCalc)
      .execute[HttpResponse]
      .logFailureReason(connectorName = "`SubmissionsConnector` on clearCalc")
      .flatMap { response =>
        if (response.status == NO_CONTENT) {
          Future.successful(Done)
        } else {
          Future.failed(UpstreamErrorResponse("", response.status))
        }
      }
}
