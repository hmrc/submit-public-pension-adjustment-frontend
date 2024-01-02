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

import models.{Enumerable, WithName}

sealed trait QuarterChargePaid

object QuarterChargePaid extends Enumerable.Implicits {

  case object AprToJul extends WithName("aprToJul") with QuarterChargePaid
  case object JulToOct extends WithName("julToOct") with QuarterChargePaid
  case object OctToJan extends WithName("octToJan") with QuarterChargePaid
  case object JanToApr extends WithName("janToApr") with QuarterChargePaid

  val values: Seq[QuarterChargePaid] = Seq(
    AprToJul,
    JulToOct,
    OctToJan,
    JanToApr
  )

  implicit val enumerable: Enumerable[QuarterChargePaid] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
