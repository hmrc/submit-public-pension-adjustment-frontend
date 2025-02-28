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

import forms.mappings.Mappings
import play.api.data.Form
import play.api.i18n.Messages
import views.helpers.ImplicitDateFormatter

import java.time.{Clock, LocalDate}
import javax.inject.Inject

class PensionSchemeMemberDOBFormProvider @Inject() (clock: Clock) extends Mappings with ImplicitDateFormatter {

  val max = LocalDate.now(clock)
  val min = LocalDate.now(clock).minusYears(130)

  def apply()(implicit messages: Messages): Form[LocalDate] = {

    val languageTag = if (messages.lang.code == "cy") "cy" else "en"
    Form(
      "value" -> localDate(
        invalidKey = "pensionSchemeMemberDOB.error.invalid",
        allRequiredKey = "pensionSchemeMemberDOB.error.required.all",
        twoRequiredKey = "pensionSchemeMemberDOB.error.required.two",
        requiredKey = "pensionSchemeMemberDOB.error.required"
      )
        .verifying(maxDate(max, "pensionSchemeMemberDOB.error.max", dateToString(max, languageTag)))
        .verifying(minDate(min, "pensionSchemeMemberDOB.error.min", dateToString(min, languageTag)))
    )
  }
}
