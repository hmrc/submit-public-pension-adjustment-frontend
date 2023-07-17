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

import bars.barsmodel.request.{BarsValidateRequest, BarsVerifyPersonalRequest}
import bars.barsmodel.response.BarsVerifyResponse
import config.BarsConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.http.HttpReads.Implicits._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/** Connects to the Bank Account Reputation Service to validate bank accounts.
  */
class BarsConnector @Inject() (
  httpClient: HttpClient,
  barsConfig: BarsConfig
)(implicit ec: ExecutionContext) {

  val baseUrl: String = barsConfig.baseUrl

  /** "The Validate Bank Details endpoint combines several functions to provide an aggregated validation result"
    */
  private val validateUrl: String = s"$baseUrl/validate/bank-details"

  def validateBankDetails(barsValidateRequest: BarsValidateRequest)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    httpClient.POST[BarsValidateRequest, HttpResponse](validateUrl, barsValidateRequest)

  /** "This endpoint checks the likely correctness of a given personal bank account and it's likely connection to the
    * given account holder (aka the subject)"
    */
  private val verifyPersonalUrl: String = s"$baseUrl/verify/personal"

  def verifyPersonal(
    barsVerifyPersonalRequest: BarsVerifyPersonalRequest
  )(implicit hc: HeaderCarrier): Future[BarsVerifyResponse] =
    httpClient
      .POST[BarsVerifyPersonalRequest, HttpResponse](verifyPersonalUrl, barsVerifyPersonalRequest)
      .map(response => response.json.as[BarsVerifyResponse])

}
