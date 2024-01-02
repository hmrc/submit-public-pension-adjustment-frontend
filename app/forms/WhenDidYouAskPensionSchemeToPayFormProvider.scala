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

import java.time.{Clock, LocalDate}
import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

import java.time.format.DateTimeFormatter

class WhenDidYouAskPensionSchemeToPayFormProvider @Inject() (clock: Clock) extends Mappings {

  def maxDate: LocalDate    = LocalDate.now(clock)
  def minDate: LocalDate    = LocalDate.of(2015, 4, 6)
  private def dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey = "whenDidYouAskPensionSchemeToPay.error.invalid",
        allRequiredKey = "whenDidYouAskPensionSchemeToPay.error.required.all",
        twoRequiredKey = "whenDidYouAskPensionSchemeToPay.error.required.two",
        requiredKey = "whenDidYouAskPensionSchemeToPay.error.required"
      ).verifying(
        maxDate(maxDate, "whenDidYouAskPensionSchemeToPay.error.afterMaximum", maxDate.format(dateFormatter)),
        minDate(minDate, "whenDidYouAskPensionSchemeToPay.error.beforeMinimum", minDate.format(dateFormatter))
      )
    )
}
