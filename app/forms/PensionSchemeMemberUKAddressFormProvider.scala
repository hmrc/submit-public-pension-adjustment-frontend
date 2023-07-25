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
import models.PensionSchemeMemberUKAddress

class PensionSchemeMemberUKAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[PensionSchemeMemberUKAddress] = Form(
    mapping(
      "addressLine1" -> text("pensionSchemeMemberUKAddress.error.addressLine1.required")
        .verifying(maxLength(100, "pensionSchemeMemberUKAddress.error.addressLine1.length")),
      "addressLine2" -> optional(
        text("pensionSchemeMemberUKAddress.error.addressLine2.required")
          .verifying(maxLength(100, "pensionSchemeMemberUKAddress.error.addressLine2.length"))
      ),
      "townOrCity"   -> text("pensionSchemeMemberUKAddress.error.townOrCity.required")
        .verifying(maxLength(100, "pensionSchemeMemberUKAddress.error.townOrCity.length")),
      "county"       -> optional(
        text("pensionSchemeMemberUKAddress.error.county.required")
          .verifying(maxLength(100, "pensionSchemeMemberUKAddress.error.county.length"))
      ),
      "postCode"     -> text("pensionSchemeMemberUKAddress.error.postCode.required")
        .verifying(
          firstError(
            maxLength(8, "pensionSchemeMemberUKAddress.error.postCode.length"),
            regexp(
              """[a-zA-Z]{1,2}[0-9][0-9a-zA-Z]? ?[0-9][a-zA-Z]{2}""",
              "pensionSchemeMemberUKAddress.error.postCode.invalid"
            )
          )
        )
    )(PensionSchemeMemberUKAddress.apply)(PensionSchemeMemberUKAddress.unapply)
  )
}
