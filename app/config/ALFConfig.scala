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

  val baseUrl: String     = servicesConfig.baseUrl("address-lookup-frontend")
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
    frontendAppConfig.signOutWithGuidance
  val serviceOrigin: String =
    configuration.get[String]("contact-frontend.serviceId")

  val cy: Lang = Lang("CY")
  val en: Lang = Lang("EN")

  // noinspection ScalaStyle
  def claimOnBehalfRequestConfig(continueUrl: String, requestHeader: RequestHeader) = {

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
             |          "navTitle": "${messagesApi("service.name")(en)}"
             |        },
             |        "countryPickerLabels": {
             |        "title": "${messagesApi("addressLookup.claimOnBehalf.countryPicker.title")(en)}",
             |        "heading": "${messagesApi("addressLookup.claimOnBehalf.countryPicker.heading")(en)}",
             |        "countryLabel": "${messagesApi("addressLookup.claimOnBehalf.countryPicker.inputLabel")(en)}"
             |      },
             |        "lookupPageLabels": {
             |          "title": "${messagesApi("addressLookup.claimOnBehalf.lookupPage.title")(en)}",
             |          "heading": "${messagesApi("addressLookup.claimOnBehalf.lookupPage.heading")(en)}",
             |          "manualAddressLinkText": "${messagesApi(
          "addressLookup.claimOnBehalf.lookupPage.manualAddressLinkText"
        )(en)}"
             |        },
             |        "editPageLabels": {
             |          "title": "${messagesApi("addressLookup.claimOnBehalf.editPage.title")(en)}",
             |          "heading": "${messagesApi("addressLookup.claimOnBehalf.editPage.heading")(en)}"
             |        },
             |        "selectPageLabels": {
             |          "title": "${messagesApi("addressLookup.claimOnBehalf.selectPage.title")(en)}",
             |          "heading": "${messagesApi("addressLookup.claimOnBehalf.selectPage.heading")(en)}",
             |          "editAddressLinkText": "${messagesApi(
          "addressLookup.claimOnBehalf.selectPage.editAddressLinkText"
        )(en)}"
             |        },
             |        "confirmPageLabels": {
             |        "title": "${messagesApi("addressLookup.claimOnBehalf.confirmPage.title")(en)}",
             |        "heading": "${messagesApi("addressLookup.claimOnBehalf.confirmPage.heading")(en)}",
             |        "changeLinkText": "${messagesApi("addressLookup.claimOnBehalf.confirmPage.changeLinkText")(en)}",
             |        "submitLabel": "${messagesApi("site.continue")(en)}"
             |      }
             |      },
             |      "cy": {
             |        "appLevelLabels": {
             |          "navTitle": "${messagesApi("service.name")(cy)}"
             |        },
             |        "countryPickerLabels": {
             |        "title": "${messagesApi("addressLookup.claimOnBehalf.countryPicker.title")(cy)}",
             |        "heading": "${messagesApi("addressLookup.claimOnBehalf.countryPicker.heading")(cy)}",
             |        "countryLabel": "${messagesApi("addressLookup.claimOnBehalf.countryPicker.inputLabel")(cy)}"
             |      },
             |        "lookupPageLabels": {
             |          "title": "${messagesApi("addressLookup.claimOnBehalf.lookupPage.title")(cy)}",
             |          "heading": "${messagesApi("addressLookup.claimOnBehalf.lookupPage.heading")(cy)}",
             |          "manualAddressLinkText": "${messagesApi(
          "addressLookup.claimOnBehalf.lookupPage.manualAddressLinkText"
        )(cy)}"
             |        },
             |        "editPageLabels": {
             |          "title": "${messagesApi("addressLookup.claimOnBehalf.editPage.title")(cy)}",
             |          "heading": "${messagesApi("addressLookup.claimOnBehalf.editPage.heading")(cy)}"
             |        },
             |        "selectPageLabels": {
             |          "title": "${messagesApi("addressLookup.claimOnBehalf.selectPage.title")(cy)}",
             |          "heading": "${messagesApi("addressLookup.claimOnBehalf.selectPage.heading")(cy)}",
             |          "editAddressLinkText": "${messagesApi(
          "addressLookup.claimOnBehalf.selectPage.editAddressLinkText"
        )(cy)}"
             |        },
             |        "confirmPageLabels": {
             |        "title": "${messagesApi("addressLookup.claimOnBehalf.confirmPage.title")(cy)}",
             |        "heading": "${messagesApi("addressLookup.claimOnBehalf.confirmPage.heading")(cy)}",
             |        "changeLinkText": "${messagesApi("addressLookup.claimOnBehalf.confirmPage.changeLinkText")(cy)}",
             |        "submitLabel": "${messagesApi("site.continue")(cy)}"
             |      }
             |      }
             |    }
             |  }""".stripMargin
    Json.parse(v2Config).as[JsObject]
  }

  // noinspection ScalaStyle
  def userAddressRequestConfig(continueUrl: String, requestHeader: RequestHeader) = {

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
         |          "navTitle": "${messagesApi("service.name")(en)}"
         |        },
         |        "countryPickerLabels": {
         |        "title": "${messagesApi("addressLookup.user.countryPicker.title")(en)}",
         |        "heading": "${messagesApi("addressLookup.user.countryPicker.heading")(en)}",
         |        "countryLabel": "${messagesApi("addressLookup.user.countryPicker.inputLabel")(en)}"
         |      },
         |        "lookupPageLabels": {
         |          "title": "${messagesApi("addressLookup.user.lookupPage.title")(en)}",
         |          "heading": "${messagesApi("addressLookup.user.lookupPage.heading")(en)}",
         |          "manualAddressLinkText": "${messagesApi("addressLookup.user.lookupPage.manualAddressLinkText")(en)}"
         |        },
         |        "editPageLabels": {
         |          "title": "${messagesApi("addressLookup.user.editPage.title")(en)}",
         |          "heading": "${messagesApi("addressLookup.user.editPage.heading")(en)}"
         |        },
         |        "selectPageLabels": {
         |          "title": "${messagesApi("addressLookup.user.selectPage.title")(en)}",
         |          "heading": "${messagesApi("addressLookup.user.selectPage.heading")(en)}",
         |          "editAddressLinkText": "${messagesApi("addressLookup.user.selectPage.editAddressLinkText")(en)}"
         |        },
         |        "confirmPageLabels": {
         |        "title": "${messagesApi("addressLookup.user.confirmPage.title")(en)}",
         |        "heading": "${messagesApi("addressLookup.user.confirmPage.heading")(en)}",
         |        "changeLinkText": "${messagesApi("addressLookup.user.confirmPage.changeLinkText")(en)}",
         |        "submitLabel": "${messagesApi("site.continue")(en)}"
         |      }
         |      },
         |      "cy": {
         |        "appLevelLabels": {
         |          "navTitle": "${messagesApi("service.name")(cy)}"
         |        },
         |        "countryPickerLabels": {
         |        "title": "${messagesApi("addressLookup.user.countryPicker.title")(cy)}",
         |        "heading": "${messagesApi("addressLookup.user.countryPicker.heading")(cy)}",
         |        "countryLabel": "${messagesApi("addressLookup.user.countryPicker.inputLabel")(cy)}"
         |      },
         |        "lookupPageLabels": {
         |          "title": "${messagesApi("addressLookup.user.lookupPage.title")(cy)}",
         |          "heading": "${messagesApi("addressLookup.user.lookupPage.heading")(cy)}",
         |          "manualAddressLinkText": "${messagesApi("addressLookup.user.lookupPage.manualAddressLinkText")(cy)}"
         |        },
         |        "editPageLabels": {
         |          "title": "${messagesApi("addressLookup.user.editPage.title")(cy)}",
         |          "heading": "${messagesApi("addressLookup.user.editPage.heading")(cy)}"
         |        },
         |        "selectPageLabels": {
         |          "title": "${messagesApi("addressLookup.user.selectPage.title")(cy)}",
         |          "heading": "${messagesApi("addressLookup.user.selectPage.heading")(cy)}",
         |          "editAddressLinkText": "${messagesApi("addressLookup.user.selectPage.editAddressLinkText")(cy)}"
         |        },
         |        "confirmPageLabels": {
         |        "title": "${messagesApi("addressLookup.user.confirmPage.title")(cy)}",
         |        "heading": "${messagesApi("addressLookup.user.confirmPage.heading")(cy)}",
         |        "changeLinkText": "${messagesApi("addressLookup.user.confirmPage.changeLinkText")(cy)}",
         |        "submitLabel": "${messagesApi("site.continue")(cy)}"
         |      }
         |      }
         |    }
         |  }""".stripMargin
    Json.parse(v2Config).as[JsObject]
  }
}
