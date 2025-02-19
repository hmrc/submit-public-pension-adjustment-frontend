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

package models.lta

import models.Enumerable

sealed trait LTAChargeType

object LTAChargeType extends Enumerable.Implicits {

  case object New extends LTAChargeType
  case object Increased extends LTAChargeType
  case object Decreased extends LTAChargeType

  val values: Seq[LTAChargeType] = Seq(
    New,
    Increased,
    Decreased
  )

  implicit lazy val enumerable: Enumerable[LTAChargeType] =
    Enumerable(values.map(v => v.toString -> v)*)

}
