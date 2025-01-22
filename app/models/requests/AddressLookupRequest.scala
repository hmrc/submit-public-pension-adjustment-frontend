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

package models.requests

import play.api.libs.json.{Format, Json, OFormat}

final case class AddressLookupRequest(
  version: Int = 2,
  options: AddressLookupOptions,
  labels: Option[Labels] = None
)

object AddressLookupRequest {
  implicit val addressLookupRequestFormat: OFormat[AddressLookupRequest] = Json.format[AddressLookupRequest]
}

final case class AddressLookupOptions(
  continueUrl: String,
  homeNavHref: String,
  signOutHref: Option[String] = None,
  accessibilityFooterUrl: Option[String] = None,
  deskProServiceName: Option[String] = None,
  showPhaseBanner: Option[Boolean] = None,
  phaseFeedbackLink: Option[String] = None,
  alphaPhase: Option[Boolean] = None,
  showBackButtons: Option[Boolean] = None,
  includeHMRCBranding: Option[Boolean] = None,
  disableTranslations: Option[Boolean] = None,
  ukMode: Option[Boolean] = None,
  selectPageConfig: Option[SelectPageConfig] = None,
  confirmPageConfig: Option[ConfirmPageConfig] = None,
  timeoutConfig: Option[TimeoutConfig] = None
)

object AddressLookupOptions {
  implicit val format: OFormat[AddressLookupOptions] = Json.format
}

final case class SelectPageConfig(proposalListLimit: Int)

object SelectPageConfig {
  implicit val selectPageConfigFormat: OFormat[SelectPageConfig] = Json.format[SelectPageConfig]
}

final case class ConfirmPageConfig(
  showChangeLink: Option[Boolean] = None,
  showSearchAgainLink: Option[Boolean] = None,
  showConfirmChangeText: Option[Boolean] = None
)

object ConfirmPageConfig {
  implicit val confirmPageConfigFormat: OFormat[ConfirmPageConfig] = Json.format[ConfirmPageConfig]
}

final case class TimeoutConfig(
  timeoutAmount: Int,
  timeoutUrl: String,
  timeoutKeepAliveUrl: Option[String] = None
)

object TimeoutConfig {
  implicit val timeoutConfigFormat: OFormat[TimeoutConfig] = Json.format[TimeoutConfig]
}

final case class Labels(
  en: Option[PageLabels] = None,
  cy: Option[PageLabels] = None
)

object Labels {
  implicit val LabelsFormat: OFormat[Labels] = Json.format[Labels]
}

final case class PageLabels(
  appLevelLabels: Option[AppLevelLabels] = None,
  countryPickerLabels: Option[CountryPickerLabels] = None,
  lookupPageLabels: Option[LookupPageLabels] = None,
  editPageLabels: Option[EditPageLabels] = None,
  selectPageLabels: Option[SelectPageLabels] = None,
  confirmPageLabels: Option[ConfirmPageLabels] = None
)

object PageLabels {
  implicit val PageLabelsFormat: OFormat[PageLabels] = Json.format[PageLabels]
}

case class AppLevelLabels(
  navTitle: Option[String] = None
)

object AppLevelLabels {
  implicit val AppLevelLabelsFormat: OFormat[AppLevelLabels] = Json.format[AppLevelLabels]
}

final case class CountryPickerLabels(
  title: Option[String] = None,
  heading: Option[String] = None,
  countryLabel: Option[String] = None
)

object CountryPickerLabels {
  implicit val CountryPickerLabelsFormat: OFormat[CountryPickerLabels] = Json.format[CountryPickerLabels]
}

final case class LookupPageLabels(
  title: Option[String] = None,
  heading: Option[String] = None,
  manualAddressLinkText: Option[String] = None
)

object LookupPageLabels {
  implicit val LookupPageLabelsFormat: OFormat[LookupPageLabels] = Json.format[LookupPageLabels]
}

final case class EditPageLabels(
  title: Option[String] = None,
  heading: Option[String] = None
)

object EditPageLabels {
  implicit val EditPageLabelsFormat: OFormat[EditPageLabels] = Json.format[EditPageLabels]
}

final case class SelectPageLabels(
  title: Option[String] = None,
  heading: Option[String] = None,
  editAddressLinkText: Option[String] = None
)

object SelectPageLabels {
  implicit val SelectPageLabelsFormat: OFormat[SelectPageLabels] = Json.format[SelectPageLabels]
}

final case class ConfirmPageLabels(
  title: Option[String] = None,
  heading: Option[String] = None,
  changeLinkText: Option[String] = None
)

object ConfirmPageLabels {
  implicit val ConfirmPageLabelsFormat: OFormat[ConfirmPageLabels] = Json.format[ConfirmPageLabels]
}
