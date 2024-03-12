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

package generators

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(ContinueChoicePage.type, JsValue)] ::
      arbitrary[(PensionSchemeMemberInternationalAddressPage.type, JsValue)] ::
      arbitrary[(PensionSchemeMemberUKAddressPage.type, JsValue)] ::
      arbitrary[(PensionSchemeMemberResidencePage.type, JsValue)] ::
      arbitrary[(MemberDateOfDeathPage.type, JsValue)] ::
      arbitrary[(BankDetailsPage.type, JsValue)] ::
      arbitrary[(HowMuchTaxReliefPage.type, JsValue)] ::
      arbitrary[(ClaimingHigherOrAdditionalTaxRateReliefPage.type, JsValue)] ::
      arbitrary[(UkAddressPage.type, JsValue)] ::
      arbitrary[(InternationalAddressPage.type, JsValue)] ::
      arbitrary[(AreYouAUKResidentPage.type, JsValue)] ::
      arbitrary[(StatusOfUserPage.type, JsValue)] ::
      arbitrary[(ClaimOnBehalfPage.type, JsValue)] ::
      arbitrary[(PensionSchemeMemberDOBPage.type, JsValue)] ::
      arbitrary[(PensionSchemeMemberNamePage.type, JsValue)] ::
      arbitrary[(PensionSchemeMemberTaxReferencePage.type, JsValue)] ::
      arbitrary[(PensionSchemeMemberNinoPage.type, JsValue)] ::
      arbitrary[(ContactNumberPage.type, JsValue)] ::
      arbitrary[(EnterAlternativeNamePage.type, JsValue)] ::
      arbitrary[(AlternativeNamePage.type, JsValue)] ::
      Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id   <- nonEmptyString
        data <- generators match {
                  case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
                  case _   => Gen.mapOf(oneOf(generators))
                }
      } yield UserAnswers(
        id = id,
        data = data.foldLeft(Json.obj()) { case (obj, (path, value)) =>
          obj.setObject(path.path, value).get
        }
      )
    }
  }
}
