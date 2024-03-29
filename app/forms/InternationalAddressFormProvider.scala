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

package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import play.api.data.Forms._
import models.InternationalAddress

class InternationalAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[InternationalAddress] = Form(
    mapping(
      "addressLine1"  -> text("internationalAddress.error.addressLine1.required")
        .verifying(maxLength(100, "internationalAddress.error.addressLine1.length")),
      "addressLine2"  -> optional(
        text("internationalAddress.error.addressLine2.required")
          .verifying(maxLength(100, "internationalAddress.error.addressLine2.length"))
      ),
      "townOrCity"    -> text("internationalAddress.error.townOrCity.required")
        .verifying(maxLength(100, "internationalAddress.error.townOrCity.length")),
      "stateOrRegion" -> optional(
        text("internationalAddress.error.stateOrRegion.required")
          .verifying(maxLength(100, "internationalAddress.error.stateOrRegion.length"))
      ),
      "postCode"      -> optional(
        text("internationalAddress.error.postCode.required")
          .verifying(maxLength(100, "internationalAddress.error.postCode.length"))
      ),
      "country"       -> text("internationalAddress.error.country.required")
        .verifying(maxLength(100, "internationalAddress.error.country.length"))
    )(InternationalAddress.apply)(InternationalAddress.unapply)
  )
}
