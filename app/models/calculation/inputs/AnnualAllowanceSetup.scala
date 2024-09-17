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

import play.api.libs.json._

case class AnnualAllowanceSetup(
  savingsStatement: Option[Boolean],
  pensionProtectedMember: Option[Boolean],
  hadAACharge: Option[Boolean],
  contributionRefunds: Option[Boolean],
  netIncomeAbove100K: Option[Boolean],
  netIncomeAbove190K: Option[Boolean],
  maybePIAIncrease: Option[MaybePIAIncrease],
  maybePIAUnchangedOrDecreased: Option[MaybePIAUnchangedOrDecreased],
  pIAAboveAnnualAllowanceIn2023: Option[Boolean],
  netIncomeAbove190KIn2023: Option[Boolean],
  flexibleAccessDcScheme: Option[Boolean],
  contribution4000ToDirectContributionScheme: Option[Boolean]
)

object AnnualAllowanceSetup {

  implicit lazy val formats: Format[AnnualAllowanceSetup] = Json.format
}
