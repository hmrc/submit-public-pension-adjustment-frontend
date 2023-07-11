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

import models.WhichPensionSchemeWillPay
import pages.behaviours.PageBehaviours

class WhichPensionSchemeWillPaySpec extends PageBehaviours {

  "WhichPensionSchemeWillPayPage" - {

    beRetrievable[WhichPensionSchemeWillPay](WhichPensionSchemeWillPayPage)

    beSettable[WhichPensionSchemeWillPay](WhichPensionSchemeWillPayPage)

    beRemovable[WhichPensionSchemeWillPay](WhichPensionSchemeWillPayPage)

    "must navigate correctly in NormalMode" - {

      "to PensionSchemeDetails when PensionScheme selected" in {

      }

      "to CYA when Private pension scheme selected" in {

      }
    }

    "must navigate correctly in CheckMode" - {

      "to CYA" in {

      }
    }
  }
}
