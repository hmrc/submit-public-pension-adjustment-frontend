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

package pages

import models.calculation.inputs._

import java.time.LocalDate

object TestData {

  val lifeTimeAllowanceWithMultipleSchemes =
    lifeTimeAllowance(Some(WhoPayingExtraLtaCharge.You), Some(LtaPensionSchemeDetails("Scheme2", "pstr2")))

  val lifeTimeAllowanceWithSingeScheme = lifeTimeAllowance(None, None)

  private def lifeTimeAllowance(
    whoPayingExtra: Option[WhoPayingExtraLtaCharge],
    pensionSchemeDetails: Option[LtaPensionSchemeDetails]
  ) = LifeTimeAllowance(
    benefitCrystallisationEventFlag = true,
    benefitCrystallisationEventDate = LocalDate.of(2017, 1, 30),
    changeInLifetimeAllowancePercentageInformedFlag = true,
    changeInTaxCharge = ChangeInTaxCharge.NewCharge,
    lifetimeAllowanceProtectionOrEnhancements = LtaProtectionOrEnhancements.Protection,
    protectionType = ProtectionType.PrimaryProtection,
    protectionReference = "originalReference",
    protectionTypeOrEnhancementChangedFlag = true,
    newProtectionTypeOrEnhancement = Some(WhatNewProtectionTypeEnhancement.EnhancedProtection),
    newProtectionTypeOrEnhancementReference = Some("newReference"),
    previousLifetimeAllowanceChargeFlag = true,
    previousLifetimeAllowanceChargePaymentMethod = Some(ExcessLifetimeAllowancePaid.Lumpsum),
    previousLifetimeAllowanceChargeAmount = Some(10000),
    previousLifetimeAllowanceChargePaidBy = Some(WhoPaidLTACharge.You),
    previousLifetimeAllowanceChargeSchemeNameAndTaxRef = Some(SchemeNameAndTaxRef("Scheme1", "pstr1")),
    newLifetimeAllowanceChargeAmount = 20000,
    newLifetimeAllowanceChargeWillBePaidBy = whoPayingExtra,
    newLifetimeAllowanceChargeSchemeNameAndTaxRef = pensionSchemeDetails
  )

}
