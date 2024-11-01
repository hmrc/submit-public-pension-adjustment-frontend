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

import java.time.{Clock, LocalDate, ZoneId, ZoneOffset}
import forms.behaviours.DateBehaviours
import play.api.data.FormError
import play.api.i18n.Messages
import play.api.test.Helpers.stubMessages

import java.time.format.DateTimeFormatter

class MemberDateOfDeathFormProviderSpec extends DateBehaviours {

  private val fixedInstant = LocalDate.now.atStartOfDay(ZoneId.systemDefault).toInstant
  private val clock        = Clock.fixed(fixedInstant, ZoneId.systemDefault)
  private implicit val messages: Messages = stubMessages()

  val form = new MemberDateOfDeathFormProvider(clock)()(messages)

  private def dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
  private val minDate       = LocalDate.now(clock).minusYears(130)
  private val maxDate       = LocalDate.now(clock)

  ".value" - {

    val validData = datesBetween(
      min = LocalDate.of(2000, 1, 1),
      max = LocalDate.now(ZoneOffset.UTC)
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "memberDateOfDeath.error.required.all")

    behave like dateFieldWithMax(
      form = form,
      key = "value",
      max = maxDate,
      formError = FormError("value", "memberDateOfDeath.error.max", Seq(maxDate.format(dateFormatter)))
    )

    behave like dateFieldWithMin(
      form = form,
      key = "value",
      min = minDate,
      formError = FormError("value", "memberDateOfDeath.error.min", Seq(minDate.format(dateFormatter)))
    )
  }
}
