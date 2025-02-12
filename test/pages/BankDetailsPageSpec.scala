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

package pages

import models.BankDetails

class BankDetailsPageSpec extends PageBehaviours {

  "BankDetailsPage" - {

    "should save Bank Detail" in {

      val updatedUserAnswers =
        emptyUserAnswers
          .set(BankDetailsPage, BankDetails("Testuser One", "111111", "11111111", None))
          .get

      updatedUserAnswers.get(BankDetailsPage) mustBe Some(
        BankDetails("Testuser One", "111111", "11111111", None)
      )
    }

  }
}
