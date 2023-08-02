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

package bars

import bars.barsmodel.request._
import bars.barsmodel.response._
import play.api.http.Status._
import play.api.libs.json._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}
import bars.barsmodel.request

import utils.HttpResponseUtils.HttpResponseOps

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class BarsService @Inject() (
  barsConnector: BarsConnector
)(implicit ec: ExecutionContext) {
  def preVerify(barsResponse: Future[HttpResponse])(implicit hc: HeaderCarrier): Future[BarsResponse] =
    barsResponse.map { httpResponse: HttpResponse =>
      httpResponse.status match {
        case OK =>
          httpResponse
            .parseJSON[BarsVerifyResponse]
            .map(VerifyResponse.apply)
            .getOrElse(throw UpstreamErrorResponse(httpResponse.body, httpResponse.status))

        case BAD_REQUEST =>
          httpResponse.json.validate[BarsErrorResponse] match {
            case JsSuccess(barsErrorResponse, _) if barsErrorResponse.code == "SORT_CODE_ON_DENY_LIST" =>
              SortCodeOnDenyList(barsErrorResponse)
            case _                                                                                     =>
              throw UpstreamErrorResponse(httpResponse.body, httpResponse.status)
          }

        case _ =>
          throw UpstreamErrorResponse(httpResponse.body, httpResponse.status)
      }
    }

  def verifyPersonal(barsResponse: Future[HttpResponse])(implicit
    hc: HeaderCarrier
  ): Future[VerifyResponse] =
    barsResponse.map(response => response.json.as[BarsVerifyResponse]).map(VerifyResponse.apply)

  def verifyBankDetails(
    bankAccount: BarsBankAccount,
    subject: BarsSubject
  )(implicit hc: HeaderCarrier): Future[Either[BarsError, VerifyResponse]] = {
    val barsResponse = barsConnector.verifyPersonal(request.BarsVerifyPersonalRequest(bankAccount, subject))
    preVerify(barsResponse).flatMap {
      case response: SortCodeOnDenyList =>
        Future.successful(Left(SortCodeOnDenyListErrorResponse(response)))
      case _                            =>
        verifyPersonal(barsResponse).map(handleVerifyResponse)
    }
  }

  private def handleVerifyResponse(response: VerifyResponse): Either[BarsError, VerifyResponse] = {
    import VerifyResponse._
    response match {
      // success
      case verifySuccess()                  => Right(response)
      // defined errors
      case thirdPartyError()                => Left(ThirdPartyError(response))
      case accountNumberIsWellFormattedNo() => Left(AccountNumberNotWellFormatted(response))
      case sortCodeIsPresentOnEiscdNo()     => Left(SortCodeNotPresentOnEiscd(response))
      case sortCodeSupportsDirectDebitNo()  => Left(SortCodeDoesNotSupportDirectDebit(response))
      case nameMatchesNo()                  => Left(NameDoesNotMatch(response))
      case accountDoesNotExist()            => Left(AccountDoesNotExist(response))
      // not an expected error response or a success response, so fallback to this
      case _                                => Left(OtherBarsError(response))
    }
  }
}
