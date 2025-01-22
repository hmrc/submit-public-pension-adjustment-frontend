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

package views.helpers

import java.time.LocalDate
import java.time.format.DateTimeFormatter

trait ImplicitDateFormatter {
  lazy val welshMonths = Map(
    1  -> "Ionawr",
    2  -> "Chwefror",
    3  -> "Mawrth",
    4  -> "Ebrill",
    5  -> "Mai",
    6  -> "Mehefin",
    7  -> "Gorffennaf",
    8  -> "Awst",
    9  -> "Medi",
    10 -> "Hydref",
    11 -> "Tachwedd",
    12 -> "Rhagfyr"
  )

  private val dateFormatter                                        = DateTimeFormatter.ofPattern("d MMMM yyyy")
  implicit def dateToString(date: LocalDate, lang: String): String =
    if (lang == "cy") s"${date.getDayOfMonth} ${welshMonths(date.getMonthValue)} ${date.getYear}"
    else dateFormatter.format(date)
}
