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
import models.bavf._
import models.requests.DataRequest
import pages.BankDetailsPage
import play.api.http.HeaderNames
import play.api.http.Status.OK
import play.api.i18n.Lang
import play.api.i18n.Lang.logger
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json._
import play.api.mvc.AnyContent
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BavfConnector @Inject() (httpClient: HttpClientV2, bavfConfig: BavfConfig, frontendAppConfig: FrontendAppConfig) {

  lazy val startURL: String    = bavfConfig.startURL
  lazy val retrieveURL: String = bavfConfig.retrieveURL

  def init(continueUrl: String, messages: Option[InitRequestMessages] = None, request: DataRequest[AnyContent])(implicit
    ec: ExecutionContext,
    hc: HeaderCarrier,
    language: Lang
  ): Future[String] = {
    val initRequest: InitRequest = requestBuilder(continueUrl, request)

    httpClient
      .post(url"$startURL")
      .withBody(Json.toJson(initRequest))
      .setHeader((HeaderNames.USER_AGENT, frontendAppConfig.userAgent))
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(bavfConfig.bavfWebBaseUrl + response.json.as[InitResponse].detailsUrl.get)
          case _  =>
            logger.error(
              s"Unexpected response from call from BAVF init : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from BAVF init",
                response.status
              )
            )
        }
      }
  }

  def complete(journeyId: String)(implicit
    ec: ExecutionContext,
    hc: HeaderCarrier
  ): Future[CompleteResponse] = {
    val url = s"$retrieveURL/$journeyId"

    httpClient
      .get(url"$url")
      .setHeader((HeaderNames.USER_AGENT, frontendAppConfig.userAgent))
      .execute[HttpResponse]
      .logFailureReason(connectorName = "`BavfConnector` on complete")
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[CompleteResponse])
          case _  =>
            logger.error(
              s"Unexpected response from call from BAVF complete : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from BAVF complete",
                response.status
              )
            )
        }
      }
  }

  private def requestBuilder(
    continueUrl: String,
    request: DataRequest[AnyContent]
  )() = {
    val bankDetailsAnswers = request.userAnswers.get(BankDetailsPage)

    Json
      .toJson(bavfConfig.initRequestConfig(continueUrl, bankDetailsAnswers))
      .as[InitRequest]
  }

}
