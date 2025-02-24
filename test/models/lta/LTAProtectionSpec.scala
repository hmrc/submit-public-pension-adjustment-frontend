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
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsValue, Json}

class LTAProtectionSpec extends SpecBase with Matchers {

  private val json: JsValue = Json.parse(
    """
      |{
      |    "protectionType": "PrimaryProtection",
      |    "protectionReference": "Some Reference"
      |}
    """.stripMargin
  )

  private val model: LTAProtection = LTAProtection(
    protectionType = LTAProtectionType.PrimaryProtection,
    protectionReference = "Some Reference"
  )

  "LTAProtection" - {
    "should deserialise from JSON" in {
      json.as[LTAProtection] `mustBe` model
    }

    "should serialise to JSON" in {
      Json.toJson(model) `mustBe` json
    }
  }
}
