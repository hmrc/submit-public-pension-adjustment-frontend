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

package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl.*
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrlPolicy.Id
import uk.gov.hmrc.play.bootstrap.binders.{AbsoluteWithHostnameFromAllowlist, RedirectUrl, RedirectUrlPolicy}

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  val host: String    = configuration.get[String]("host")
  val appName: String = configuration.get[String]("appName")

  private val contactHost                  = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "submit-public-pension-adjustment-frontend"

  def feedbackUrl(implicit request: RequestHeader): String = {
    val backUrl: String                  = host + request.uri
    val allowedRedirectUrls: Seq[String] = configuration.get[Seq[String]]("urls.allowedRedirects")
    val policy: RedirectUrlPolicy[Id]    = AbsoluteWithHostnameFromAllowlist(allowedRedirectUrls*)
    val safeBackUrl                      = RedirectUrl(backUrl).get(policy).encodedUrl
    s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier&backUrl=$safeBackUrl"
  }

  val baseUrl: String                     = configuration.get[String]("urls.base")
  val loginUrl: String                    = configuration.get[String]("urls.login")
  val calculateFrontend: String           = configuration.get[String]("urls.calculateFrontend")
  val confidenceUpliftUrl: String         = configuration.get[String]("urls.confidenceUplift")
  val upliftCompletionUrl                 = configuration.get[String]("urls.upliftCompletion")
  val upliftFailureUrl                    = configuration.get[String]("urls.upliftFailure")
  val landingPageLoginContinueUrl: String = configuration.get[String]("urls.landingPageLoginContinue")
  val requiredAuthConfidenceLevel         = configuration.get[String]("required-auth-confidence-level")
  val origin                              = configuration.get[String]("origin")
  val signOutUrl: String                  = configuration.get[String]("urls.signOut")
  val signOutWithGuidance: String         = configuration.get[String]("urls.signOutWithGuidance")
  val timeoutSignedOut: String            = configuration.get[String]("urls.timeoutSignedOut")
  val redirectToStartPage: String         = configuration.get[String]("urls.redirectToStartPage")

  private val exitSurveyBaseUrl: String = configuration.get[Service]("microservice.services.feedback-frontend").baseUrl
  val exitSurveyUrl: String             = s"$exitSurveyBaseUrl/feedback/submit-public-pension-adjustment-frontend"

  val cppaBaseUrl: String =
    configuration.get[Service]("microservice.services.calculate-public-pension-adjustment").baseUrl

  val sppaBaseUrl: String =
    configuration.get[Service]("microservice.services.submit-public-pension-adjustment").baseUrl

  val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "en" -> Lang("en"),
    "cy" -> Lang("cy")
  )

  val userAgent: String = configuration.get[String]("user-agent")

  val timeout: Int   = configuration.get[Int]("timeout-dialog.timeout")
  val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")

}
