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

package viewmodels.checkAnswers.helpers

import base.SpecBase
import views.helpers.ImplicitDateFormatter

import java.time.LocalDate

class ImplicitDateFormatterSpec extends SpecBase with ImplicitDateFormatter {

  "dateToString" - {

    val date: LocalDate = LocalDate.parse("2020-09-29")

    "correctly format a date when lang is English" in {
      dateToString(date, "en") mustBe "29 September 2020"
    }

    "correctly format a date when lang is Welsh" in {
      dateToString(date, "cy") mustBe "29 Medi 2020"
    }

    "default to English if the language is neither English or Welsh" in {
      dateToString(date, "da") mustBe "29 September 2020"
    }
  }
}
