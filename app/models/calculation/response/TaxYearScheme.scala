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

package models.calculation.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class TaxYearScheme(
  name: String,
  pensionSchemeTaxReference: String,
  revisedPensionInputAmount: Int,
  chargePaidByScheme: Int,
  revisedPensionInput2016PostAmount: Option[Int]
)

object TaxYearScheme {

  implicit lazy val formats: Format[TaxYearScheme] = (
    (__ \ "name").format[String] and
      (__ \ "pensionSchemeTaxReference").format[String] and
      (__ \ "revisedPensionInputAmount").format[Int] and
      (__ \ "chargePaidByScheme").format[Int] and
      (__ \ "revisedPensionInput2016PostAmount").formatNullable[Int]
  )(TaxYearScheme.apply, o => Tuple.fromProductTyped(o))
}
