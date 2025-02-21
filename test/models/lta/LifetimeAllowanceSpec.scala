/*
 * Copyright 2025 HM Revenue & Customs
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

import base.SpecBase
import models.lta.LTAChargeHowPaid.LumpSum
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

class LifetimeAllowanceSpec extends SpecBase with Matchers {

  private val json: JsValue = Json.parse(
    """
      |{
      |    "benefitCrystallisationEventDate": "2017-01-30",
      |    "chargeType": "New",
      |    "protection": [{"protectionType":"PrimaryProtection","protectionReference":"Some Reference"}],
      |    "changedProtection": [{"protectionType":"FixedProtection","protectionReference":"Other Reference"}],
      |    "charge": {"amount":100,"who":"PensionScheme","chargePaidByScheme":{"name":"Scheme1","taxReference":"pstr1","howPaid":"LumpSum"}}
      |}
    """.stripMargin
  )

  private val model: LifetimeAllowance = LifetimeAllowance(
    benefitCrystallisationEventDate = LocalDate.of(2017, 1, 30),
    chargeType = LTAChargeType.New,
    protection = List(LTAProtection(LTAProtectionType.PrimaryProtection, "Some Reference")),
    changedProtection = List(LTAProtection(LTAProtectionType.FixedProtection, "Other Reference")),
    charge = LTACharge(100, LTAChargeWhoPays.PensionScheme, Some(LTAChargePaidByScheme("Scheme1", "pstr1", LumpSum)))
  )

  "LifetimeAllowance" - {
    "should deserialise from JSON" in {
      json.as[LifetimeAllowance] `mustBe` model
    }

    "should serialise to JSON" in {
      Json.toJson(model) `mustBe` json
    }
  }
}