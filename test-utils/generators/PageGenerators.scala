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

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryContinueChoicePage: Arbitrary[ContinueChoicePage.type] =
    Arbitrary(ContinueChoicePage)

  implicit lazy val arbitraryPensionSchemeMemberInternationalAddressPage
    : Arbitrary[PensionSchemeMemberInternationalAddressPage.type] =
    Arbitrary(PensionSchemeMemberInternationalAddressPage)

  implicit lazy val arbitraryPensionSchemeMemberUKAddressPage: Arbitrary[PensionSchemeMemberUKAddressPage.type] =
    Arbitrary(PensionSchemeMemberUKAddressPage)

  implicit lazy val arbitraryPensionSchemeMemberResidencePage: Arbitrary[PensionSchemeMemberResidencePage.type] =
    Arbitrary(PensionSchemeMemberResidencePage)

  implicit lazy val arbitraryMemberDateOfDeathPage: Arbitrary[MemberDateOfDeathPage.type] =
    Arbitrary(MemberDateOfDeathPage)

  implicit lazy val arbitraryBankDetailsPage: Arbitrary[BankDetailsPage.type] =
    Arbitrary(BankDetailsPage)

  implicit lazy val arbitraryWhenWillYouAskPensionSchemeToPayPage
    : Arbitrary[WhenWillYouAskPensionSchemeToPayPage.type] =
    Arbitrary(WhenWillYouAskPensionSchemeToPayPage)

  implicit lazy val arbitraryWhenDidYouAskPensionSchemeToPayPage: Arbitrary[WhenDidYouAskPensionSchemeToPayPage.type] =
    Arbitrary(WhenDidYouAskPensionSchemeToPayPage)

  implicit lazy val arbitraryAskedPensionSchemeToPayTaxChargePage
    : Arbitrary[AskedPensionSchemeToPayTaxChargePage.type] =
    Arbitrary(AskedPensionSchemeToPayTaxChargePage)

  implicit lazy val arbitraryStatusOfUserPage: Arbitrary[StatusOfUserPage.type] =
    Arbitrary(StatusOfUserPage)

  implicit lazy val arbitraryClaimOnBehalfPage: Arbitrary[ClaimOnBehalfPage.type] =
    Arbitrary(ClaimOnBehalfPage)

  implicit lazy val arbitraryHowMuchTaxReliefPage: Arbitrary[HowMuchTaxReliefPage.type] =
    Arbitrary(HowMuchTaxReliefPage)

  implicit lazy val arbitraryClaimingHigherOrAdditionalTaxRateReliefPage
    : Arbitrary[ClaimingHigherOrAdditionalTaxRateReliefPage.type] =
    Arbitrary(ClaimingHigherOrAdditionalTaxRateReliefPage)

  implicit lazy val arbitraryReformPensionSchemeReferencePage: Arbitrary[ReformPensionSchemeReferencePage.type] =
    Arbitrary(ReformPensionSchemeReferencePage)

  implicit lazy val arbitraryLegacyPensionSchemeReferencePage: Arbitrary[LegacyPensionSchemeReferencePage.type] =
    Arbitrary(LegacyPensionSchemeReferencePage)

  implicit lazy val arbitraryUkAddressPage: Arbitrary[UkAddressPage.type] =
    Arbitrary(UkAddressPage)

  implicit lazy val arbitraryInternationalAddressPage: Arbitrary[InternationalAddressPage.type] =
    Arbitrary(InternationalAddressPage)

  implicit lazy val arbitraryAreYouAUKResidentPage: Arbitrary[AreYouAUKResidentPage.type] =
    Arbitrary(AreYouAUKResidentPage)

  implicit lazy val arbitraryWhichPensionSchemeWillPayPage: Arbitrary[WhichPensionSchemeWillPayPage.type] =
    Arbitrary(WhichPensionSchemeWillPayPage)

  implicit lazy val arbitraryPensionSchemeDetailsPage: Arbitrary[PensionSchemeDetailsPage.type] =
    Arbitrary(PensionSchemeDetailsPage)

  implicit lazy val arbitraryWhoWillPayPage: Arbitrary[WhoWillPayPage.type] =
    Arbitrary(WhoWillPayPage)

  implicit lazy val arbitraryPensionSchemeMemberDOBPage: Arbitrary[PensionSchemeMemberDOBPage.type] =
    Arbitrary(PensionSchemeMemberDOBPage)

  implicit lazy val arbitraryPensionSchemeMemberNamePage: Arbitrary[PensionSchemeMemberNamePage.type] =
    Arbitrary(PensionSchemeMemberNamePage)

  implicit lazy val arbitraryContactNumberPage: Arbitrary[ContactNumberPage.type] =
    Arbitrary(ContactNumberPage)

  implicit lazy val arbitraryEnterAlternativeNamePage: Arbitrary[EnterAlternativeNamePage.type] =
    Arbitrary(EnterAlternativeNamePage)

  implicit lazy val arbitraryAlternativeNamePage: Arbitrary[AlternativeNamePage.type] =
    Arbitrary(AlternativeNamePage)

  implicit lazy val arbitraryPensionSchemeMemberTaxReferencePage: Arbitrary[PensionSchemeMemberTaxReferencePage.type] =
    Arbitrary(PensionSchemeMemberTaxReferencePage)

  implicit lazy val arbitraryPensionSchemeMemberNinoPage: Arbitrary[PensionSchemeMemberNinoPage.type] =
    Arbitrary(PensionSchemeMemberNinoPage)

  // scala fmt ignore
}
