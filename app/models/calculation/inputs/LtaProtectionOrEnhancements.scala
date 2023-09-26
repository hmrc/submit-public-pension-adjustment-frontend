/*
 * Copyright 2023 HM Revenue & Customs
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

sealed trait LtaProtectionOrEnhancements

object LtaProtectionOrEnhancements extends Enumerable.Implicits {

  case object Protection extends WithName("protection") with LtaProtectionOrEnhancements
  case object Enhancements extends WithName("enhancements") with LtaProtectionOrEnhancements
  case object Both extends WithName("both") with LtaProtectionOrEnhancements
  case object No extends WithName("no") with LtaProtectionOrEnhancements

  val values: Seq[LtaProtectionOrEnhancements] = Seq(
    Protection,
    Enhancements,
    Both,
    No
  )

  implicit val enumerable: Enumerable[LtaProtectionOrEnhancements] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
