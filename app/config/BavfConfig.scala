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

package config

import models.BankDetails
import play.api.Configuration
import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class BavfConfig @Inject() (
  config: Configuration,
  servicesConfig: ServicesConfig,
  messagesApi: MessagesApi,
  frontendAppConfig: FrontendAppConfig
) {
  val bavfWebBaseUrl: String = servicesConfig.baseUrl("bank-account-verification-web")

  val startURL: String    = s"$bavfWebBaseUrl/api/v3/init"
  val retrieveURL: String = s"$bavfWebBaseUrl/api/v3/complete"

  val signOutWithGuidance: String =
    frontendAppConfig.signOutWithGuidance
  val baseUrl: String             = frontendAppConfig.baseUrl
  val timeoutSignedOut: String    = frontendAppConfig.timeoutSignedOut

  val bavfReturnNormalMode: String =
    config.get[String]("urls.bavfReturnNormalMode")
  val bavfReturnCheckMode: String  =
    config.get[String]("urls.bavfReturnCheckMode")

  // noinspection ScalaStyle
  def initRequestConfig(continueUrl: String, bankDetailsAnswers: Option[BankDetails])() = {
    val en = Lang("EN")
    val cy = Lang("CY")

    val welshMessages =
      if (frontendAppConfig.languageTranslationEnabled) {
        s""",
           |  "cy": {
           |    "service.name": "${messagesApi("service.name")(cy)}",
           |    "label.accountDetails.heading": "${messagesApi("bankDetails.heading")(cy)}",
           |    "label.submitLabel": "${messagesApi("site.continue")(cy)}"
           |  }""".stripMargin
      } else {
        ""
      }

    val barsRequestJson =
      s"""{
         |  "serviceIdentifier": "calculate-public-pension-adjustment",
         |  "continueUrl": "$continueUrl",
         |  "messages": {
         |    "en": {
         |      "service.name": "${messagesApi("service.name")(en)}",
         |      "label.accountDetails.heading": "${messagesApi("bankDetails.heading")(en)}",
         |      "label.submitLabel": "${messagesApi("site.continue")(en)}"
         |    }$welshMessages
         |  },
         |  "timeoutConfig": {
         |    "timeoutUrl": "$timeoutSignedOut",
         |    "timeoutAmount": 890
         |  },
         |  "prepopulatedData": {
         |    "accountType": "personal",
         |    "name": "${bankDetailsAnswers.map(_.accountName).getOrElse("")}",
         |    "sortCode": "${bankDetailsAnswers.map(_.sortCode).getOrElse("")}",
         |    "accountNumber": "${bankDetailsAnswers.map(_.accountNumber).getOrElse("")}",
         |    "rollNumber": "${bankDetailsAnswers.flatMap(_.rollNumber).getOrElse("")}"
         |  },
         |  "signOutUrl": "$signOutWithGuidance"
         |}""".stripMargin

    Json.parse(barsRequestJson).as[JsObject]

  }

}
