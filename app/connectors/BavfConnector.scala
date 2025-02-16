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

import config.{BavfConfig, FrontendAppConfig}
import connectors.ConnectorFailureLogger.FromResultToConnectorFailureLogger
import models.BankDetails
import models.bavf._
import play.api.Logging
import play.api.http.HeaderNames
import play.api.http.Status.OK
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import uk.gov.hmrc.http.HttpReads.Implicits._
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.ws.writeableOf_JsValue

class BavfConnector @Inject() (httpClient: HttpClientV2, bavfConfig: BavfConfig, frontendAppConfig: FrontendAppConfig)
    extends Logging {

  lazy val startURL: String       = bavfConfig.startURL
  lazy val retrieveURL: String    = bavfConfig.retrieveURL
  lazy val bavfWebBaseUrl: String = bavfConfig.bavfWebBaseUrl

  def initialiseJourney(continueUrl: String, bankDetailsAnswers: Option[BankDetails])(implicit
    ec: ExecutionContext,
    hc: HeaderCarrier
  ): Future[String] = {
    val initRequest: BavfInitRequest = requestBuilder(continueUrl, bankDetailsAnswers)()

    httpClient
      .post(url"$startURL")
      .withBody(Json.toJson(initRequest))
      .setHeader((HeaderNames.USER_AGENT, frontendAppConfig.userAgent))
      .execute[HttpResponse]
      .logFailureReason(connectorName = "`BavfConnector` on start")
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(bavfWebBaseUrl + response.json.as[BavfInitResponse].detailsUrl.get)
          case _  =>
            logger.error(
              s"Unexpected response from call from BAVF initialiseJourney : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from BAVF initialiseJourney",
                response.status
              )
            )
        }
      }
  }

  def retrieveBarsDetails(journeyId: String)(implicit
    ec: ExecutionContext,
    hc: HeaderCarrier
  ): Future[BavfCompleteResponse] = {
    val url = s"$retrieveURL/$journeyId"

    httpClient
      .get(url"$url")
      .setHeader((HeaderNames.USER_AGENT, frontendAppConfig.userAgent))
      .execute[HttpResponse]
      .logFailureReason(connectorName = "`BavfConnector` on retrieveBarsDetails")
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[BavfCompleteResponse])
          case _  =>
            logger.error(
              s"Unexpected response from call from BAVF retrieveBarsDetails : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from BAVF retrieveBarsDetails",
                response.status
              )
            )
        }
      }
  }

  private def requestBuilder(
    continueUrl: String,
    bankDetailsAnswers: Option[BankDetails]
  )() =
    Json
      .toJson(bavfConfig.initRequestConfig(continueUrl, bankDetailsAnswers)())
      .as[BavfInitRequest]

}
