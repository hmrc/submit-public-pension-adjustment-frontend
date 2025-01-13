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

import play.api.Configuration
import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
case class ALFConfig @Inject() (
  configuration: Configuration,
  servicesConfig: ServicesConfig,
  messagesApi: MessagesApi,
  frontendAppConfig: FrontendAppConfig
) {

  val baseUrl: String     = servicesConfig.baseUrl("alf")
  val startURL: String    = s"$baseUrl/api/init"
  val retrieveURL: String = s"$baseUrl/api/confirmed"

  val keepAlive: String                                  = configuration.get[String]("urls.keepAlive")
  val addressLookupReturnClaimOnBehalfNormalMode: String =
    configuration.get[String]("urls.addressLookupReturnClaimOnBehalfNormalMode")
  val addressLookupReturnClaimOnBehalfCheckMode: String  =
    configuration.get[String]("urls.addressLookupReturnClaimOnBehalfCheckMode")
  val addressLookupReturnUserAddressNormalMode: String   =
    configuration.get[String]("urls.addressLookupReturnUserAddressNormalMode")
  val addressLookupReturnUserAddressCheckMode: String    =
    configuration.get[String]("urls.addressLookupReturnUserAddressCheckMode")
  val accessibilityStatementUrl: String                  =
    configuration.get[String]("accessibility-statement.service-path")

  val homePage: String      =
    frontendAppConfig.landingPageLoginContinueUrl
  val signOutPage: String   =
    frontendAppConfig.signOutUrl
  val serviceOrigin: String =
    configuration.get[String]("contact-frontend.serviceId")

  // noinspection ScalaStyle
  def claimOnBehalfRequestConfig(continueUrl: String, language: Lang, requestHeader: RequestHeader) = {

    val feedbackUrl: String =
      frontendAppConfig.feedbackUrl(requestHeader)

    val v2Config =
      s"""{
             |  "version": 2,
             |  "options": {
             |    "continueUrl": "$continueUrl",
             |    "homeNavHref": "$homePage",
             |    "signOutHref": "$signOutPage",
             |    "accessibilityFooterUrl": "$accessibilityStatementUrl",
             |    "deskProServiceName": "$serviceOrigin",
             |    "showPhaseBanner": true,
             |    "phaseFeedbackLink": "$feedbackUrl",
             |    "alphaPhase": false,
             |    "showBackButtons": true,
             |    "includeHMRCBranding": false,
             |    "disableTranslations": true,
             |    "ukMode": false,
             |    "selectPageConfig": {
             |      "proposalListLimit": 50,
             |      "showSearchLinkAgain": true
             |    },
             |    "confirmPageConfig": {
             |      "showChangeLink": true,
             |      "showSubHeadingAndInfo": true,
             |      "showSearchAgainLink": true,
             |      "showConfirmChangeText": true
             |    },
             |    "timeoutConfig": {
             |      "timeoutAmount": 890,
             |      "timeoutUrl": "${controllers.auth.routes.SignedOutController.onPageLoad.url}",
             |      "timeoutKeepAliveUrl": "$keepAlive"
             |    }
             |},
             |    "labels": {
             |      "en": {
             |        "appLevelLabels": {
             |          "navTitle": "${messagesApi("index.title")(language)}"
             |        },
             |        "countryPickerLabels": {
             |        "title": "${messagesApi("addressLookup.claimOnBehalf.countryPicker.title")(language)}",
             |        "heading": "${messagesApi("addressLookup.claimOnBehalf.countryPicker.heading")(language)}",
             |        "countryLabel": "${messagesApi("addressLookup.claimOnBehalf.countryPicker.inputLabel")(language)}"
             |      },
             |        "lookupPageLabels": {
             |          "title": "${messagesApi("addressLookup.claimOnBehalf.lookupPage.title")(language)}",
             |          "heading": "${messagesApi("addressLookup.claimOnBehalf.lookupPage.heading")(language)}",
             |          "manualAddressLinkText": "${messagesApi(
          "addressLookup.claimOnBehalf.lookupPage.manualAddressLinkText"
        )(language)}"
             |        },
             |        "editPageLabels": {
             |          "title": "${messagesApi("addressLookup.claimOnBehalf.editPage.title")(language)}",
             |          "heading": "${messagesApi("addressLookup.claimOnBehalf.editPage.heading")(language)}"
             |        },
             |        "selectPageLabels": {
             |          "title": "${messagesApi("addressLookup.claimOnBehalf.selectPage.title")(language)}",
             |          "heading": "${messagesApi("addressLookup.claimOnBehalf.selectPage.heading")(language)}",
             |          "editAddressLinkText": "${messagesApi(
          "addressLookup.claimOnBehalf.selectPage.editAddressLinkText"
        )(language)}"
             |        },
             |        "confirmPageLabels": {
             |        "title": "${messagesApi("addressLookup.claimOnBehalf.confirmPage.title")(language)}",
             |        "heading": "${messagesApi("addressLookup.claimOnBehalf.confirmPage.heading")(language)}",
             |        "changeLinkText": "${messagesApi("addressLookup.claimOnBehalf.confirmPage.changeLinkText")(
          language
        )}"
             |      }
             |      },
             |      "cy": {
             |        "appLevelLabels": {
             |          "navTitle": "${messagesApi("index.title")(language)}"
             |        },
             |        "countryPickerLabels": {
             |        "title": "${messagesApi("addressLookup.claimOnBehalf.countryPicker.title")(language)}",
             |        "heading": "${messagesApi("addressLookup.claimOnBehalf.countryPicker.heading")(language)}",
             |        "countryLabel": "${messagesApi("addressLookup.claimOnBehalf.countryPicker.inputLabel")(language)}"
             |      },
             |        "lookupPageLabels": {
             |          "title": "${messagesApi("addressLookup.claimOnBehalf.lookupPage.title")(language)}",
             |          "heading": "${messagesApi("addressLookup.claimOnBehalf.lookupPage.heading")(language)}",
             |          "manualAddressLinkText": "${messagesApi(
          "addressLookup.claimOnBehalf.lookupPage.manualAddressLinkText"
        )(language)}"
             |        },
             |        "editPageLabels": {
             |          "title": "${messagesApi("addressLookup.claimOnBehalf.editPage.title")(language)}",
             |          "heading": "${messagesApi("addressLookup.claimOnBehalf.editPage.heading")(language)}"
             |        },
             |        "selectPageLabels": {
             |          "title": "${messagesApi("addressLookup.claimOnBehalf.selectPage.title")(language)}",
             |          "heading": "${messagesApi("addressLookup.claimOnBehalf.selectPage.heading")(language)}",
             |          "editAddressLinkText": "${messagesApi(
          "addressLookup.claimOnBehalf.selectPage.editAddressLinkText"
        )(language)}"
             |        },
             |        "confirmPageLabels": {
             |        "title": "${messagesApi("addressLookup.claimOnBehalf.confirmPage.title")(language)}",
             |        "heading": "${messagesApi("addressLookup.claimOnBehalf.confirmPage.heading")(language)}",
             |        "changeLinkText": "${messagesApi("addressLookup.claimOnBehalf.confirmPage.changeLinkText")(
          language
        )}"
             |      }
             |      }
             |    }
             |  }""".stripMargin
    Json.parse(v2Config).as[JsObject]
  }

  // noinspection ScalaStyle
  def userAddressRequestConfig(continueUrl: String, language: Lang, requestHeader: RequestHeader) = {

    val feedbackUrl: String =
      frontendAppConfig.feedbackUrl(requestHeader)

    val v2Config =
      s"""{
         |  "version": 2,
         |  "options": {
         |    "continueUrl": "$continueUrl",
         |    "homeNavHref": "$homePage",
         |    "signOutHref": "$signOutPage",
         |    "accessibilityFooterUrl": "$accessibilityStatementUrl",
         |    "deskProServiceName": "$serviceOrigin",
         |    "showPhaseBanner": true,
         |    "phaseFeedbackLink": "$feedbackUrl",
         |    "alphaPhase": false,
         |    "showBackButtons": true,
         |    "includeHMRCBranding": false,
         |    "disableTranslations": true,
         |    "ukMode": false,
         |    "selectPageConfig": {
         |      "proposalListLimit": 50,
         |      "showSearchLinkAgain": true
         |    },
         |    "confirmPageConfig": {
         |      "showChangeLink": true,
         |      "showSubHeadingAndInfo": true,
         |      "showSearchAgainLink": true,
         |      "showConfirmChangeText": true
         |    },
         |    "timeoutConfig": {
         |      "timeoutAmount": 890,
         |      "timeoutUrl": "${controllers.auth.routes.SignedOutController.onPageLoad.url}",
         |      "timeoutKeepAliveUrl": "$keepAlive"
         |    }
         |},
         |    "labels": {
         |      "en": {
         |        "appLevelLabels": {
         |          "navTitle": "${messagesApi("index.title")(language)}"
         |        },
         |        "countryPickerLabels": {
         |        "title": "${messagesApi("addressLookup.user.countryPicker.title")(language)}",
         |        "heading": "${messagesApi("addressLookup.user.countryPicker.heading")(language)}",
         |        "countryLabel": "${messagesApi("addressLookup.user.countryPicker.inputLabel")(language)}"
         |      },
         |        "lookupPageLabels": {
         |          "title": "${messagesApi("addressLookup.user.lookupPage.title")(language)}",
         |          "heading": "${messagesApi("addressLookup.user.lookupPage.heading")(language)}",
         |          "manualAddressLinkText": "${messagesApi("addressLookup.user.lookupPage.manualAddressLinkText")(
          language
        )}"
         |        },
         |        "editPageLabels": {
         |          "title": "${messagesApi("addressLookup.user.editPage.title")(language)}",
         |          "heading": "${messagesApi("addressLookup.user.editPage.heading")(language)}"
         |        },
         |        "selectPageLabels": {
         |          "title": "${messagesApi("addressLookup.user.selectPage.title")(language)}",
         |          "heading": "${messagesApi("addressLookup.user.selectPage.heading")(language)}",
         |          "editAddressLinkText": "${messagesApi("addressLookup.user.selectPage.editAddressLinkText")(
          language
        )}"
         |        },
         |        "confirmPageLabels": {
         |        "title": "${messagesApi("addressLookup.user.confirmPage.title")(language)}",
         |        "heading": "${messagesApi("addressLookup.user.confirmPage.heading")(language)}",
         |        "changeLinkText": "${messagesApi("addressLookup.user.confirmPage.changeLinkText")(language)}"
         |      }
         |      },
         |      "cy": {
         |        "appLevelLabels": {
         |          "navTitle": "${messagesApi("index.title")(language)}"
         |        },
         |        "countryPickerLabels": {
         |        "title": "${messagesApi("addressLookup.user.countryPicker.title")(language)}",
         |        "heading": "${messagesApi("addressLookup.user.countryPicker.heading")(language)}",
         |        "countryLabel": "${messagesApi("addressLookup.user.countryPicker.inputLabel")(language)}"
         |      },
         |        "lookupPageLabels": {
         |          "title": "${messagesApi("addressLookup.user.lookupPage.title")(language)}",
         |          "heading": "${messagesApi("addressLookup.user.lookupPage.heading")(language)}",
         |          "manualAddressLinkText": "${messagesApi("addressLookup.user.lookupPage.manualAddressLinkText")(
          language
        )}"
         |        },
         |        "editPageLabels": {
         |          "title": "${messagesApi("addressLookup.user.editPage.title")(language)}",
         |          "heading": "${messagesApi("addressLookup.user.editPage.heading")(language)}"
         |        },
         |        "selectPageLabels": {
         |          "title": "${messagesApi("addressLookup.user.selectPage.title")(language)}",
         |          "heading": "${messagesApi("addressLookup.user.selectPage.heading")(language)}",
         |          "editAddressLinkText": "${messagesApi("addressLookup.user.selectPage.editAddressLinkText")(
          language
        )}"
         |        },
         |        "confirmPageLabels": {
         |        "title": "${messagesApi("addressLookup.user.confirmPage.title")(language)}",
         |        "heading": "${messagesApi("addressLookup.user.confirmPage.heading")(language)}",
         |        "changeLinkText": "${messagesApi("addressLookup.user.confirmPage.changeLinkText")(language)}"
         |      }
         |      }
         |    }
         |  }""".stripMargin
    Json.parse(v2Config).as[JsObject]
  }
}
