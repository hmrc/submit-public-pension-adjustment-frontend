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

import models.{CheckMode, NormalMode, PensionSchemeDetails, Period}

class WhichPensionSchemeWillPayPageSpec extends PageBehaviours {

  "WhichPensionSchemeWillPayPage" - {

    beRetrievable[String](WhichPensionSchemeWillPayPage(Period._2020))

    beSettable[String](WhichPensionSchemeWillPayPage(Period._2020))

    beRemovable[String](WhichPensionSchemeWillPayPage(Period._2020))

    "must navigate correctly in NormalMode" - {

      "to PensionSchemeDetails when Private pension scheme selected" in {
        val ua     = emptyUserAnswers
          .set(
            WhichPensionSchemeWillPayPage(Period._2020),
            "Private pension scheme"
          )
          .success
          .value
        val result = WhichPensionSchemeWillPayPage(Period._2020).navigate(NormalMode, ua).url

        checkNavigation(result, "/submission-service/2020/private-scheme-name-reference")
      }

      "must navigate correctly in NormalMode" - {

        "to PensionSchemeDetails when Private pension scheme selected" in {
          val ua     = emptyUserAnswers
            .set(
              WhichPensionSchemeWillPayPage(Period._2020),
              "Cynllun pensiwn preifat"
            )
            .success
            .value
          val result = WhichPensionSchemeWillPayPage(Period._2020).navigate(NormalMode, ua).url

          checkNavigation(result, "/submission-service/2020/private-scheme-name-reference")
        }

        "to Asked pension scheme to pay charge page when public Pension scheme selected" in {
          val ua     = emptyUserAnswers
            .set(
              WhichPensionSchemeWillPayPage(Period._2020),
              "Scheme1 / 00348916RT"
            )
            .success
            .value
          val result = WhichPensionSchemeWillPayPage(Period._2020).navigate(NormalMode, ua).url

          checkNavigation(result, "/submission-service/2020/asked-pension-scheme-to-pay-tax-charge")
        }

        "to JourneyRecoveryPage when not selected" in {
          val ua     = emptyUserAnswers
          val result = WhichPensionSchemeWillPayPage(Period._2020).navigate(NormalMode, ua).url

          checkNavigation(result, "/there-is-a-problem")
        }
      }

      "must navigate correctly in CheckMode" - {

        "to PensionSchemeDetails when Private pension scheme selected" in {
          val ua     = emptyUserAnswers
            .set(
              WhichPensionSchemeWillPayPage(Period._2020),
              "Private pension scheme"
            )
            .success
            .value
          val result = WhichPensionSchemeWillPayPage(Period._2020).navigate(CheckMode, ua).url

          checkNavigation(result, "/submission-service/2020/change-private-scheme-name-reference")
        }

        "to PensionSchemeDetails when Private pension scheme selected (WELSH)" in {
          val ua     = emptyUserAnswers
            .set(
              WhichPensionSchemeWillPayPage(Period._2020),
              "Cynllun pensiwn preifat"
            )
            .success
            .value
          val result = WhichPensionSchemeWillPayPage(Period._2020).navigate(CheckMode, ua).url

          checkNavigation(result, "/submission-service/2020/change-private-scheme-name-reference")
        }

        "to CYA when public pension selected asked pension scheme to pay page already answered" in {
          val ua     = emptyUserAnswers
            .set(
              AskedPensionSchemeToPayTaxChargePage(Period._2020),
              true
            )
            .success
            .value
            .set(
              WhichPensionSchemeWillPayPage(Period._2020),
              "Scheme1 / 00348916RT"
            )
            .success
            .value
          val result = WhichPensionSchemeWillPayPage(Period._2020).navigate(CheckMode, ua).url

          checkNavigation(result, "/check-your-answers")
        }

        "to asked pension scheme to pay page when not already answered" in {
          val ua     = emptyUserAnswers
            .set(
              WhichPensionSchemeWillPayPage(Period._2020),
              "Scheme1 / 00348916RT"
            )
            .success
            .value
          val result = WhichPensionSchemeWillPayPage(Period._2020).navigate(CheckMode, ua).url

          checkNavigation(result, "/submission-service/2020/change-asked-pension-scheme-to-pay-tax-charge")
        }

        "to JourneyRecovery when not answered" in {
          val ua     = emptyUserAnswers
          val result = WhichPensionSchemeWillPayPage(Period._2020).navigate(CheckMode, ua).url

          checkNavigation(result, "/there-is-a-problem")
        }
      }

      "clean up" - {
        "must cleanup correctly when user selects scheme from calculation service" in {

          val ua = emptyUserAnswers
            .set(WhichPensionSchemeWillPayPage(Period._2020), "Private pension scheme")
            .success
            .value
            .set(PensionSchemeDetailsPage(Period._2020), PensionSchemeDetails("name", "pstr"))
            .success
            .value

          val cleanedUserAnswers =
            WhichPensionSchemeWillPayPage(Period._2020).cleanup(Some("Scheme1 / 00348916RT"), ua).success.value

          cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2020)) `mustBe` None

        }

        "must clean up correctly when user reselect private pension scheme" in {

          val ua = emptyUserAnswers
            .set(WhichPensionSchemeWillPayPage(Period._2020), "Private pension scheme")
            .success
            .value
            .set(PensionSchemeDetailsPage(Period._2020), PensionSchemeDetails("name", "pstr"))
            .success
            .value

          val cleanedUserAnswers =
            WhichPensionSchemeWillPayPage(Period._2020).cleanup(Some("Private pension scheme"), ua).success.value

          cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2020)) `mustBe` Some(
            PensionSchemeDetails("name", "pstr")
          )
        }
      }

      "must cleanup correctly when user selects scheme from calculation service (WELSH)" in {

        val ua = emptyUserAnswers
          .set(WhichPensionSchemeWillPayPage(Period._2020), "Private pension scheme")
          .success
          .value
          .set(PensionSchemeDetailsPage(Period._2020), PensionSchemeDetails("name", "pstr"))
          .success
          .value

        val cleanedUserAnswers =
          WhichPensionSchemeWillPayPage(Period._2020).cleanup(Some("Scheme1 / 00348916RT"), ua).success.value

        cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2020)) `mustBe` None

      }

      "must clean up correctly when user reselect private pension scheme (WELSH)" in {

        val ua = emptyUserAnswers
          .set(WhichPensionSchemeWillPayPage(Period._2020), "Cynllun pensiwn preifat")
          .success
          .value
          .set(PensionSchemeDetailsPage(Period._2020), PensionSchemeDetails("name", "pstr"))
          .success
          .value

        val cleanedUserAnswers =
          WhichPensionSchemeWillPayPage(Period._2020).cleanup(Some("Cynllun pensiwn preifat"), ua).success.value

        cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2020)) `mustBe` Some(
          PensionSchemeDetails("name", "pstr")
        )
      }
    }
  }
}
