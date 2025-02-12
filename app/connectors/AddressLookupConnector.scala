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
import play.api.Logging
import play.api.http.HeaderNames
import uk.gov.hmrc.http.HttpReads.Implicits._
import play.api.http.Status.{ACCEPTED, OK}
import play.api.libs.json.Json
import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressLookupConnector @Inject() (
  httpClient2: HttpClientV2,
  ALFConfig: ALFConfig,
  frontendAppConfig: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends Logging {

  lazy val startURL: String    = ALFConfig.startURL
  lazy val retrieveURL: String = ALFConfig.retrieveURL

  def initialiseJourney(
    requestHeader: RequestHeader,
    isClaimOnBehalf: Boolean,
    returnUrl: String
  )(implicit hc: HeaderCarrier): Future[String] = {

    val request = requestBuilder(isClaimOnBehalf, returnUrl, requestHeader)
    httpClient2
      .post(url"$startURL")
      .withBody(Json.toJson(request))
      .setHeader((HeaderNames.USER_AGENT, frontendAppConfig.userAgent))
      .execute[HttpResponse]
      .logFailureReason(connectorName = "`AddressLookupConnector` on start")
      .flatMap { response =>
        response.status match {
          case ACCEPTED =>
            response.header("LOCATION") match {
              case Some(redirectUrl) => Future.successful(redirectUrl)
              case None              =>
                logger.error(
                  s"ALF post call response missing location : ${response.status}"
                )
                Future.failed(
                  UpstreamErrorResponse(
                    "ALF post call response missing location",
                    response.status
                  )
                )
            }
          case _        =>
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
  )(implicit hc: HeaderCarrier): Future[AddressLookupConfirmation] =
    httpClient2
      .get(url"$retrieveURL?id=$id")
      .setHeader((HeaderNames.USER_AGENT, frontendAppConfig.userAgent))
      .execute[HttpResponse]
      .logFailureReason(connectorName = "`AddressLookupConnector` on retrieve")
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[AddressLookupConfirmation])
          case _  =>
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

  private def requestBuilder(
    isClaimOnBehalf: Boolean,
    returnURL: String,
    requestHeader: RequestHeader
  ) =
    if (isClaimOnBehalf) {
      Json
        .toJson(ALFConfig.claimOnBehalfRequestConfig(continueUrl = s"$returnURL", requestHeader))
        .as[AddressLookupRequest]
    } else {
      Json
        .toJson(ALFConfig.userAddressRequestConfig(continueUrl = s"$returnURL", requestHeader))
        .as[AddressLookupRequest]
    }
}
