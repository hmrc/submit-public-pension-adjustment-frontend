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
import models.Enumerable
import play.api.http.HeaderNames
import play.api.http.Status.OK
import play.api.i18n.Lang.logger
import play.api.libs.json._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BavfConnector @Inject()(httpClient: HttpClientV2, appConfig: BavfConfig, frontendAppConfig: FrontendAppConfig) {

  lazy val startURL: String = appConfig.startURL
  lazy val retrieveURL: String = appConfig.retrieveURL

  def init(continueUrl: String,
           messages: Option[InitRequestMessages] = None,
           customisationsUrl: Option[String] = None,
           prepopulatedData: Option[InitRequestPrepopulatedData] = None)(
            implicit ec: ExecutionContext,
            hc: HeaderCarrier
          ): Future[String] = {
    import HttpReads.Implicits.readRaw
    import InitRequest.writes

    val baseUrl: String = appConfig.startURL
    val retrieveURL: String = s"$baseUrl/api/confirmed"

    val request = InitRequest(
      "bank-account-verification-example-frontend",
      continueUrl,
      messages,
      customisationsUrl,
      address = Some(InitRequestAddress(List("Line 1", "Line 2"), Some("Town"), Some("Postcode"))),
      timeoutConfig = Some(InitRequestTimeoutConfig("/bank-account-verification-example-frontend", 240, None)),
      prepopulatedData = prepopulatedData,
      signOutUrl = Some("/sign-out"),
      maxCallCount = Some(5),
      maxCallCountRedirectUrl = Some("/too-many-attempts"))

    val url = s"${baseUrl}/api/v2/init"
    httpClient.post(url"$startURL")
      .withBody(Json.toJson(request))
      .setHeader((HeaderNames.USER_AGENT, frontendAppConfig.userAgent))
      .execute[HttpResponse]
      .logFailureReason(connectorName = "`BavfConnector` on start")
      .flatMap { response =>
        response.status match {
          case OK =>
            Some(response.json.as[InitResponse].startUrl) match {
              case Some(redirectUrl) => Future.successful(redirectUrl)
              case _ =>
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
}

//  def complete(journeyId: String)(
//      implicit ec: ExecutionContext,
//      hc: HeaderCarrier
//  ): Future[Option[CompleteResponse]] = {
//    import CompleteResponse._
//    import HttpReads.Implicits.readRaw
//
//    val url = s"${appConfig.bavfApiBaseUrl}/api/v3/complete/$journeyId"
//    httpClient.GET[HttpResponse](url).map {
//      case r if r.status == 200 =>
//        Some(r.json.as[CompleteResponse])
//      case _                    =>
//        None
//    }
//  }
//}

case class InitRequest(serviceIdentifier: String,
                       continueUrl: String,
                       messages: Option[InitRequestMessages] = None,
                       customisationsUrl: Option[String] = None,
                       prepopulatedData: Option[InitRequestPrepopulatedData] = None,
                       address: Option[InitRequestAddress] = None,
                       timeoutConfig: Option[InitRequestTimeoutConfig] = None,
                       signOutUrl: Option[String] = None,
                       maxCallCount: Option[Int] = None,
                       maxCallCountRedirectUrl: Option[String] = None)

case class InitRequestMessages(en: JsObject, cy: Option[JsObject] = None)

case class InitRequestAddress(lines: List[String], town: Option[String], postcode: Option[String])

case class InitRequestTimeoutConfig(timeoutUrl: String, timeoutAmount: Int, timeoutKeepAliveUrl: Option[String])

case class InitResponse(journeyId: String, startUrl: String, completeUrl: String, detailsUrl: Option[String])

case class InitRequestPrepopulatedData(accountType: Option[String] = None,
                                       name: Option[String] = None,
                                       sortCode: Option[String] = None,
                                       accountNumber: Option[String] = None,
                                       rollNumber: Option[String] = None)

object InitRequestPrepopulatedData {
  def from(accountType: Option[String] = None,
           name: Option[String] = None,
           sortCode: Option[String] = None,
           accountNumber: Option[String] = None,
           rollNumber: Option[String] = None): Option[InitRequestPrepopulatedData] = {
    val definedValues = List(accountType, name, sortCode, accountNumber, rollNumber).flatten

    if (definedValues.isEmpty) None
    else Some(InitRequestPrepopulatedData(accountType = accountType, name = name, sortCode = sortCode, accountNumber = accountNumber, rollNumber = rollNumber))
  }

  def from(accountType: String, name: String, sortCode: String, accountNumber: String, rollNumber: Option[String]): Option[InitRequestPrepopulatedData] = {
    from(Some(accountType), Some(name), Some(sortCode), Some(accountNumber), rollNumber)
  }
}

object InitResponse {
  implicit def writes: OWrites[InitResponse] = Json.writes[InitResponse]

  implicit def reads: Reads[InitResponse] = Json.reads[InitResponse]
}

object InitRequest {
  implicit val messagesWrites: OWrites[InitRequestMessages] = Json.writes[InitRequestMessages]
  implicit val addressWrites: OWrites[InitRequestAddress] = Json.writes[InitRequestAddress]
  implicit val prepopulatedDataWrites: OWrites[InitRequestPrepopulatedData] = Json.writes[InitRequestPrepopulatedData]
  implicit val timeoutConfigWrites: OWrites[InitRequestTimeoutConfig] = Json.writes[InitRequestTimeoutConfig]
  implicit val writes: Writes[InitRequest] = Json.writes[InitRequest]
}

case class CompleteResponse(accountType: String,
                            personal: Option[PersonalCompleteResponse],
                            business: Option[BusinessCompleteResponse])

case class CompleteResponseAddress(lines: List[String], town: Option[String], postcode: Option[String]) {
  override def toString: String = {
    (lines ++ Seq(town, postcode).flatten).mkString("<br>")
  }
}

object CompleteResponse {
  implicit val addressReads: Reads[CompleteResponseAddress] = Json.reads[CompleteResponseAddress]
  implicit val addressWrites: Writes[CompleteResponseAddress] = Json.writes[CompleteResponseAddress]

  implicit val reads: Reads[CompleteResponse] = Json.reads[CompleteResponse]
  implicit val writes: Writes[CompleteResponse] = Json.writes[CompleteResponse]
}

case class ExtendedCompleteResponse(completeResponse: CompleteResponse, extraInformation: Option[String])

object ExtendewdCompleteResponse {

  implicit val reads: Reads[ExtendedCompleteResponse] = Json.reads[ExtendedCompleteResponse]
  implicit val writes: Writes[ExtendedCompleteResponse] = Json.writes[ExtendedCompleteResponse]
}

case class PersonalCompleteResponse(address: Option[CompleteResponseAddress],
                                    accountName: String,
                                    sortCode: String,
                                    accountNumber: String,
                                    accountNumberIsWellFormatted: ReputationResponseEnum,
                                    rollNumber: Option[String],
                                    accountExists: Option[ReputationResponseEnum],
                                    nameMatches: Option[ReputationResponseEnum],
                                    matchedAccountName: Option[String],
                                    nonStandardAccountDetailsRequiredForBacs: Option[ReputationResponseEnum],
                                    sortCodeBankName: Option[String],
                                    sortCodeSupportsDirectDebit: Option[ReputationResponseEnum],
                                    sortCodeSupportsDirectCredit: Option[ReputationResponseEnum], iban: Option[String])

object PersonalCompleteResponse {
  implicit val addressReads: Reads[CompleteResponseAddress] = Json.reads[CompleteResponseAddress]
  implicit val addressWrites: Writes[CompleteResponseAddress] = Json.writes[CompleteResponseAddress]
  implicit val reads: Reads[PersonalCompleteResponse] = Json.reads[PersonalCompleteResponse]
  implicit val writes: Writes[PersonalCompleteResponse] = Json.writes[PersonalCompleteResponse]
}

case class BusinessCompleteResponse(address: Option[CompleteResponseAddress],
                                    companyName: String,
                                    sortCode: String,
                                    accountNumber: String,
                                    rollNumber: Option[String],
                                    accountNumberIsWellFormatted: ReputationResponseEnum,
                                    accountExists: Option[ReputationResponseEnum],
                                    nameMatches: Option[ReputationResponseEnum],
                                    matchedAccountName: Option[String],
                                    nonStandardAccountDetailsRequiredForBacs: Option[ReputationResponseEnum],
                                    sortCodeBankName: Option[String],
                                    sortCodeSupportsDirectDebit: Option[ReputationResponseEnum],
                                    sortCodeSupportsDirectCredit: Option[ReputationResponseEnum], iban: Option[String])

object BusinessCompleteResponse {
  implicit val addressReads: Reads[CompleteResponseAddress] = Json.reads[CompleteResponseAddress]
  implicit val addressWrites: Writes[CompleteResponseAddress] = Json.writes[CompleteResponseAddress]
  implicit val completeResponseReads: Reads[BusinessCompleteResponse] = Json.reads[BusinessCompleteResponse]
  implicit val completeResponseWrites: Writes[BusinessCompleteResponse] = Json.writes[BusinessCompleteResponse]
}

sealed trait ReputationResponseEnum

object ReputationResponseEnum extends Enumerable.Implicits {

  case object Yes extends WithName("yes") with ReputationResponseEnum

  case object No extends WithName("no") with ReputationResponseEnum

  case object Partial extends WithName("partial") with ReputationResponseEnum

  case object Indeterminate
      extends WithName("indeterminate")
          with ReputationResponseEnum

  case object Inapplicable
      extends WithName("inapplicable")
          with ReputationResponseEnum

  case object Error extends WithName("error") with ReputationResponseEnum

  val values: Seq[ReputationResponseEnum] =
    Seq(Yes, No, Partial, Indeterminate, Inapplicable, Error)

  implicit val enumerable: Enumerable[ReputationResponseEnum] =
    Enumerable(values.map(v => v.toString -> v): _*)
}

class WithName(string: String) {
  override val toString: String = string
}
