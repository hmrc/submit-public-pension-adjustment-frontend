/*
 * Copyright 2025 HM Revenue & Customs
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

import base.SpecBase
import models.BankDetails
import org.mockito.MockitoSugar
import play.api.Configuration
import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.JsObject
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class BavfConfigSpec extends SpecBase with MockitoSugar {

  val mockConfig: Configuration                = mock[Configuration]
  val mockServicesConfig: ServicesConfig       = mock[ServicesConfig]
  val mockMessagesApi: MessagesApi             = mock[MessagesApi]
  val mockFrontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

  val continueUrl = "https://example.com/continue"
  val en          = Lang("EN")
  val cy          = Lang("CY")

  when(mockMessagesApi("service.name")(en)).thenReturn("Service Name EN")
  when(mockMessagesApi("bankDetails.heading")(en)).thenReturn("Account Details EN")
  when(mockMessagesApi("site.continue")(en)).thenReturn("Continue EN")
  when(mockMessagesApi("service.name")(cy)).thenReturn("Service Name CY")
  when(mockMessagesApi("bankDetails.heading")(cy)).thenReturn("Account Details CY")
  when(mockMessagesApi("site.continue")(cy)).thenReturn("Continue CY")

  val bankDetails: Option[BankDetails] = Some(BankDetails("John Doe", "11111", "22222", Some("33333")))

  "BavfConfig" - {

    "initRequestConfig" - {

      "include Welsh translations when languageTranslationEnabled is true" in {
        when(mockFrontendAppConfig.languageTranslationEnabled).thenReturn(true)

        val resultJson: JsObject =
          new BavfConfig(mockConfig, mockServicesConfig, mockMessagesApi, mockFrontendAppConfig)
            .initRequestConfig(continueUrl, bankDetails)()

        val messages = (resultJson \ "messages").as[JsObject]

        (messages \ "en" \ "service.name").as[String] mustBe "Service Name EN"
        (messages \ "cy" \ "service.name").as[String] mustBe "Service Name CY"

      }

      "not include Welsh translations when languageTranslationEnabled is false" in {
        when(mockFrontendAppConfig.languageTranslationEnabled).thenReturn(false)

        val resultJson: JsObject =
          new BavfConfig(mockConfig, mockServicesConfig, mockMessagesApi, mockFrontendAppConfig)
            .initRequestConfig(continueUrl, bankDetails)()

        val messages = (resultJson \ "messages").as[JsObject]

        (messages \ "en" \ "service.name").as[String] mustBe "Service Name EN"
        (messages \ "cy").isDefined mustBe false

      }

    }
  }
}
