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

package utils

import base.SpecBase
import utils.CurrencyFormatter.currencyFormat

class CurrencyFormatterSpec extends SpecBase {

  "must format string with exclusively digits correctly" in {
    currencyFormat("123") `mustBe` "£123"
    currencyFormat("123456") `mustBe` "£123,456"
    currencyFormat("1234567") `mustBe` "£1,234,567"
    currencyFormat("12345678") `mustBe` "£12,345,678"
    currencyFormat("123456789") `mustBe` "£123,456,789"
  }

  "must not format any other strings" in {
    currencyFormat("notApplicable") `mustBe` "notApplicable"
    currencyFormat("1two3") `mustBe` "1two3"
  }
}
