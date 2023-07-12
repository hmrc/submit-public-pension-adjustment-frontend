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

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.UkAddress

class UkAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[UkAddress] = Form(
    mapping(
      "addressLine1" -> text("ukAddress.error.addressLine1.required")
        .verifying(maxLength(100, "ukAddress.error.addressLine1.length")),
      "addressLine2" -> optional(
        text("ukAddress.error.addressLine2.required")
          .verifying(maxLength(100, "ukAddress.error.addressLine2.length"))
      ),
      "townOrCity"   -> text("ukAddress.error.townOrCity.required")
        .verifying(maxLength(100, "ukAddress.error.townOrCity.length")),
      "county"       -> optional(
        text("ukAddress.error.county.required")
          .verifying(maxLength(100, "ukAddress.error.county.length"))
      ),
      "postCode"     -> text("ukAddress.error.postCode.required")
        .verifying(maxLength(100, "ukAddress.error.postCode.length"))
    )(UkAddress.apply)(UkAddress.unapply)
  )
}
