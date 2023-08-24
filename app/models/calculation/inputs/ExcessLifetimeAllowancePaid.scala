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

sealed trait ExcessLifetimeAllowancePaid

object ExcessLifetimeAllowancePaid extends Enumerable.Implicits {

  case object Annualpayment extends WithName("annualPayment") with ExcessLifetimeAllowancePaid
  case object Lumpsum extends WithName("lumpSum") with ExcessLifetimeAllowancePaid

  val values: Seq[ExcessLifetimeAllowancePaid] = Seq(
    Annualpayment,
    Lumpsum
  )

  implicit val enumerable: Enumerable[ExcessLifetimeAllowancePaid] =
    Enumerable(values.map(v => v.toString -> v): _*)
}