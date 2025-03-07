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

import models._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryConfirmEditAnswersUserAnswersEntry: Arbitrary[(ConfirmEditAnswersPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConfirmEditAnswersPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryConfirmRestartAnswersUserAnswersEntry
    : Arbitrary[(ConfirmRestartAnswersPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ConfirmRestartAnswersPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryContinueChoiceUserAnswersEntry: Arbitrary[(ContinueChoicePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ContinueChoicePage.type]
        value <- arbitrary[ContinueChoice].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPensionSchemeMemberInternationalAddressUserAnswersEntry
    : Arbitrary[(PensionSchemeMemberInternationalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PensionSchemeMemberInternationalAddressPage.type]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPensionSchemeMemberUKAddressUserAnswersEntry
    : Arbitrary[(PensionSchemeMemberUKAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PensionSchemeMemberUKAddressPage.type]
        value <- arbitrary[UkAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPensionSchemeMemberResidenceUserAnswersEntry
    : Arbitrary[(PensionSchemeMemberResidencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PensionSchemeMemberResidencePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryMemberDateOfDeathUserAnswersEntry: Arbitrary[(MemberDateOfDeathPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[MemberDateOfDeathPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryBankDetailsUserAnswersEntry: Arbitrary[(BankDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[BankDetailsPage.type]
        value <- arbitrary[BankDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhenWillYouAskPensionSchemeToPayUserAnswersEntry
    : Arbitrary[(WhenWillYouAskPensionSchemeToPayPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhenWillYouAskPensionSchemeToPayPage.type]
        value <- arbitrary[WhenWillYouAskPensionSchemeToPay].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhenDidYouAskPensionSchemeToPayUserAnswersEntry
    : Arbitrary[(WhenDidYouAskPensionSchemeToPayPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhenDidYouAskPensionSchemeToPayPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAskedPensionSchemeToPayTaxChargeUserAnswersEntry
    : Arbitrary[(AskedPensionSchemeToPayTaxChargePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AskedPensionSchemeToPayTaxChargePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryStatusOfUserUserAnswersEntry: Arbitrary[(StatusOfUserPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[StatusOfUserPage.type]
        value <- arbitrary[StatusOfUser].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryClaimOnBehalfUserAnswersEntry: Arbitrary[(ClaimOnBehalfPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ClaimOnBehalfPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryContactNumberUserAnswersEntry: Arbitrary[(ContactNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ContactNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryEnterAlternativeNameUserAnswersEntry: Arbitrary[(EnterAlternativeNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[EnterAlternativeNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAlternativeNameUserAnswersEntry: Arbitrary[(AlternativeNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AlternativeNamePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHowMuchTaxReliefUserAnswersEntry: Arbitrary[(HowMuchTaxReliefPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HowMuchTaxReliefPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryClaimingHigherOrAdditionalTaxRateReliefUserAnswersEntry
    : Arbitrary[(ClaimingHigherOrAdditionalTaxRateReliefPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ClaimingHigherOrAdditionalTaxRateReliefPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryReformPensionSchemeReferenceUserAnswersEntry
    : Arbitrary[(ReformPensionSchemeReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ReformPensionSchemeReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryLegacyPensionSchemeReferenceUserAnswersEntry
    : Arbitrary[(LegacyPensionSchemeReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[LegacyPensionSchemeReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryUkAddressUserAnswersEntry: Arbitrary[(UkAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[UkAddressPage.type]
        value <- arbitrary[UkAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryInternationalAddressUserAnswersEntry: Arbitrary[(InternationalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[InternationalAddressPage.type]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAreYouAUKResidentUserAnswersEntry: Arbitrary[(AreYouAUKResidentPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AreYouAUKResidentPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPensionSchemeDetailsUserAnswersEntry: Arbitrary[(PensionSchemeDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PensionSchemeDetailsPage.type]
        value <- arbitrary[PensionSchemeDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhoWillPayUserAnswersEntry: Arbitrary[(WhoWillPayPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhoWillPayPage.type]
        value <- arbitrary[WhoWillPay].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPensionSchemeMemberDOBUserAnswersEntry
    : Arbitrary[(PensionSchemeMemberDOBPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PensionSchemeMemberDOBPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPensionSchemeMemberNameUserAnswersEntry
    : Arbitrary[(PensionSchemeMemberNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PensionSchemeMemberNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPensionSchemeMemberTaxReferenceUserAnswersEntry
    : Arbitrary[(PensionSchemeMemberTaxReferencePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PensionSchemeMemberTaxReferencePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPensionSchemeMemberNinoUserAnswersEntry
    : Arbitrary[(PensionSchemeMemberNinoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PensionSchemeMemberNinoPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  // scala fmt ignore
}
