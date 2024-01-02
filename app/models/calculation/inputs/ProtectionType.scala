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

sealed trait ProtectionType

object ProtectionType extends Enumerable.Implicits {

  case object EnhancedProtection extends WithName("enhancedProtection") with ProtectionType
  case object PrimaryProtection extends WithName("primaryProtection") with ProtectionType
  case object FixedProtection extends WithName("fixedProtection") with ProtectionType
  case object FixedProtection2014 extends WithName("fixedProtection2014") with ProtectionType
  case object FixedProtection2016 extends WithName("fixedProtection2016") with ProtectionType
  case object IndividualProtection2014 extends WithName("individualProtection2014") with ProtectionType
  case object IndividualProtection2016 extends WithName("individualProtection2016") with ProtectionType

  val values: Seq[ProtectionType] = Seq(
    EnhancedProtection,
    PrimaryProtection,
    FixedProtection,
    FixedProtection2014,
    FixedProtection2016,
    IndividualProtection2014,
    IndividualProtection2016
  )

  implicit val enumerable: Enumerable[ProtectionType] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
