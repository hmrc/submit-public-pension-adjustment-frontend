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

import config.{ALFConfig, FrontendAppConfig}
import connectors.ConnectorFailureLogger.FromResultToConnectorFailureLogger
import models.requests.{AddressLookupConfirmation, AddressLookupRequest}
import play.api.{Configuration, Logging}
import play.api.http.Status.{ACCEPTED, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class AddressLookupConnector @Inject() (
                                httpClient2: HttpClientV2,
                                frontendAppConfig: FrontendAppConfig,
                                configuration: Configuration,
                                ALFConfig: ALFConfig
                              )(implicit ec: ExecutionContext) extends Logging {

  val baseUrl: String = ALFConfig.baseUrl

  val startURL: String = s"$baseUrl/api/init"

  lazy val retrieveURL: String = s"$baseUrl/api/confirmed"


  def start(
             request: AddressLookupRequest
           )(implicit hc: HeaderCarrier) = {
    httpClient2
      .post(url"$startURL")
      .withBody(Json.toJson(request))
      .execute[HttpResponse]
      .logFailureReason(connectorName = "`AddressLookupConnector` on start")
      .flatMap { response =>
        response.status match {
          case ACCEPTED =>
            Future.successful(response.header("LOCATION").get)
          case _ =>
            logger.error(
              s"Unexpected response from call from ALF : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from ALF",
                response.status
              )
            )
        }
      }
  }

  def retrieveAddress(
                       id: String
                     )(implicit hc: HeaderCarrier): Future[AddressLookupConfirmation] = {
    httpClient2
      .get(url"${retrieveURL}?id=$id")
      .execute[HttpResponse]
      .logFailureReason(connectorName = "`AddressLookupConnector` on retrieve")
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[AddressLookupConfirmation])
          case _ =>
            logger.error(
              s"Unexpected response from call from ALF retrieve : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from ALF retrieve",
                response.status
              )
            )
        }
      }
  }
}


