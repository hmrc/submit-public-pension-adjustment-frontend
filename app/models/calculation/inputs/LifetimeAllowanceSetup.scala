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

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class LifetimeAllowanceSetup(
  benefitCrystallisationEventFlag: Option[Boolean],
  previousLTACharge: Option[Boolean],
  changeInLifetimeAllowancePercentageInformedFlag: Option[Boolean],
  increaseInLTACharge: Option[Boolean],
  newLTACharge: Option[Boolean],
  multipleBenefitCrystallisationEventFlag: Option[Boolean],
  otherSchemeNotification: Option[Boolean]
)

object LifetimeAllowanceSetup {

  implicit lazy val formats: Format[LifetimeAllowanceSetup] = (
    (__ \ "benefitCrystallisationEventFlag").formatNullable[Boolean] and
      (__ \ "previousLTACharge").formatNullable[Boolean] and
      (__ \ "changeInLifetimeAllowancePercentageInformedFlag").formatNullable[Boolean] and
      (__ \ "increaseInLTACharge").formatNullable[Boolean] and
      (__ \ "newLTACharge").formatNullable[Boolean] and
      (__ \ "multipleBenefitCrystallisationEventFlag").formatNullable[Boolean] and
      (__ \ "otherSchemeNotification").formatNullable[Boolean]
  )(LifetimeAllowanceSetup.apply, o => Tuple.fromProductTyped(o))
}
