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

package models.calculation.inputs

import play.api.libs.json.*

import scala.math.Ordered.orderingToOrdered

case class TaxYear2011To2015(
  pensionInputAmount: Int,
  period: Period
) extends TaxYear

object TaxYear2011To2015 {
  implicit lazy val reads: Reads[TaxYear2011To2015] = {

    import play.api.libs.functional.syntax.*

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p >= Period._2011 && p <= Period._2015 =>
          Reads(_ => JsSuccess(p))
        case _                                           =>
          Reads(_ => JsError("taxYear must fall between `2011`-`2015`"))
      } andKeep {
      (
        (__ \ "pensionInputAmount").read[Int] and
          (__ \ "period").read[Period]
      )(TaxYear2011To2015.apply)
    }
  }

  implicit lazy val writes: Writes[TaxYear2011To2015] = {

    import play.api.libs.functional.syntax.*

    (
      (__ \ "pensionInputAmount").write[Int] and
        (__ \ "period").write[Period]
    )(a => (a.pensionInputAmount, a.period))
  }

}
