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

import forms.behaviours.OptionFieldBehaviours
import models.ContinueChoice
import play.api.data.FormError

class ContinueChoiceFormProviderSpec extends OptionFieldBehaviours {

  val form = new ContinueChoiceFormProvider()()

  ".value" - {

    val fieldName   = "value"
    val requiredKey = "continueChoice.error.required"

    behave like optionsField[ContinueChoice](
      form,
      fieldName,
      validValues = ContinueChoice.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
