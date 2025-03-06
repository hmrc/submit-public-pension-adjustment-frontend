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

import java.time.LocalDate

case class LifeTimeAllowance(
  benefitCrystallisationEventDate: LocalDate,
  lifetimeAllowanceProtectionOrEnhancements: LtaProtectionOrEnhancements,
  protectionType: Option[ProtectionType],
  protectionReference: Option[String],
  protectionTypeEnhancementChanged: ProtectionEnhancedChanged,
  newProtectionTypeOrEnhancement: Option[WhatNewProtectionTypeEnhancement],
  newProtectionTypeOrEnhancementReference: Option[String],
  previousLifetimeAllowanceChargeFlag: Boolean,
  previousLifetimeAllowanceChargePaymentMethod: Option[ExcessLifetimeAllowancePaid],
  previousLifetimeAllowanceChargePaidBy: Option[WhoPaidLTACharge],
  previousLifetimeAllowanceChargeSchemeNameAndTaxRef: Option[SchemeNameAndTaxRef],
  newLifetimeAllowanceChargeWillBePaidBy: Option[WhoPayingExtraLtaCharge],
  newLifetimeAllowanceChargeSchemeNameAndTaxRef: Option[LtaPensionSchemeDetails],
  newLifeTimeAllowanceAdditions: NewLifeTimeAllowanceAdditions
)

object LifeTimeAllowance {

  implicit lazy val formats: Format[LifeTimeAllowance] = (
    (__ \ "benefitCrystallisationEventDate").format[LocalDate] and
      (__ \ "lifetimeAllowanceProtectionOrEnhancements").format[LtaProtectionOrEnhancements] and
      (__ \ "protectionType").formatNullable[ProtectionType] and
      (__ \ "protectionReference").formatNullable[String] and
      (__ \ "protectionTypeEnhancementChanged").format[ProtectionEnhancedChanged] and
      (__ \ "newProtectionTypeOrEnhancement").formatNullable[WhatNewProtectionTypeEnhancement] and
      (__ \ "newProtectionTypeOrEnhancementReference").formatNullable[String] and
      (__ \ "previousLifetimeAllowanceChargeFlag").format[Boolean] and
      (__ \ "previousLifetimeAllowanceChargePaymentMethod").formatNullable[ExcessLifetimeAllowancePaid] and
      (__ \ "previousLifetimeAllowanceChargePaidBy").formatNullable[WhoPaidLTACharge] and
      (__ \ "previousLifetimeAllowanceChargeSchemeNameAndTaxRef").formatNullable[SchemeNameAndTaxRef] and
      (__ \ "newLifetimeAllowanceChargeWillBePaidBy").formatNullable[WhoPayingExtraLtaCharge] and
      (__ \ "newLifetimeAllowanceChargeSchemeNameAndTaxRef").formatNullable[LtaPensionSchemeDetails] and
      (__ \ "newLifeTimeAllowanceAdditions").format[NewLifeTimeAllowanceAdditions]
  )(LifeTimeAllowance.apply, o => Tuple.fromProductTyped(o))
}

case class NewLifeTimeAllowanceAdditions(
  enhancementType: Option[EnhancementType],
  internationalEnhancementReference: Option[String],
  pensionCreditReference: Option[String],
  newEnhancementType: Option[NewEnhancementType],
  newInternationalEnhancementReference: Option[String],
  newPensionCreditReference: Option[String],
  lumpSumValue: Option[Int],
  annualPaymentValue: Option[Int],
  userSchemeDetails: Option[UserSchemeDetails],
  quarterChargePaid: Option[QuarterChargePaid],
  yearChargePaid: Option[YearChargePaid],
  newExcessLifetimeAllowancePaid: Option[NewExcessLifetimeAllowancePaid],
  newLumpSumValue: Option[Int],
  newAnnualPaymentValue: Option[Int]
)

object NewLifeTimeAllowanceAdditions {

  implicit lazy val formats: Format[NewLifeTimeAllowanceAdditions] = (
    (__ \ "enhancementType").formatNullable[EnhancementType] and
      (__ \ "internationalEnhancementReference").formatNullable[String] and
      (__ \ "pensionCreditReference").formatNullable[String] and
      (__ \ "newEnhancementType").formatNullable[NewEnhancementType] and
      (__ \ "newInternationalEnhancementReference").formatNullable[String] and
      (__ \ "newPensionCreditReference").formatNullable[String] and
      (__ \ "lumpSumValue").formatNullable[Int] and
      (__ \ "annualPaymentValue").formatNullable[Int] and
      (__ \ "userSchemeDetails").formatNullable[UserSchemeDetails] and
      (__ \ "quarterChargePaid").formatNullable[QuarterChargePaid] and
      (__ \ "yearChargePaid").formatNullable[YearChargePaid] and
      (__ \ "newExcessLifetimeAllowancePaid").formatNullable[NewExcessLifetimeAllowancePaid] and
      (__ \ "newLumpSumValue").formatNullable[Int] and
      (__ \ "newAnnualPaymentValue").formatNullable[Int]
  )(NewLifeTimeAllowanceAdditions.apply, o => Tuple.fromProductTyped(o))
}
