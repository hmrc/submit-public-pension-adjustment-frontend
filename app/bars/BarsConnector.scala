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

package bars

import bars.barsmodel.request.BarsVerifyPersonalRequest
import config.BarsConfig
import play.api.http.HeaderNames
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/** Connects to the Bank Account Reputation Service to validate bank accounts.
  */
class BarsConnector @Inject() (
  httpClient2: HttpClientV2,
  barsConfig: BarsConfig
)(implicit ec: ExecutionContext) {

  val baseUrl: String = barsConfig.baseUrl

  /** "This endpoint checks the likely correctness of a given personal bank account and it's likely connection to the
    * given account holder (aka the subject)"
    */
  private val verifyPersonalUrl: String = s"$baseUrl/verify/personal"

  def verifyPersonal(
    barsVerifyPersonalRequest: BarsVerifyPersonalRequest
  )(implicit hc: HeaderCarrier): Future[HttpResponse] =
    httpClient2
      .post(url"$verifyPersonalUrl")
      .withBody(Json.toJson(barsVerifyPersonalRequest))
      .setHeader((HeaderNames.USER_AGENT, "calculate-public-pension-adjustment"))
      .execute[HttpResponse]
}
