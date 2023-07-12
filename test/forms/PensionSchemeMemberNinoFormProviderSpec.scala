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

package forms

import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.data.FormError
import uk.gov.hmrc.domain.Nino

class PensionSchemeMemberNinoFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "pensionSchemeMemberNino.error.required"
  val lengthKey   = "pensionSchemeMemberNino.error.length"
  val maxLength   = 10

  val form = new PensionSchemeMemberNinoFormProvider()()

  ".value" - {

    val fieldName         = "value"
    val ninoGen           = arbitrary[Nino].map(_.value)
    val ninoWithSpacesGen = for {
      spaceBefore <- Gen.stringOf(Gen.const(' '))
      spaceAfter  <- Gen.stringOf(Gen.const(' '))
      nino        <- ninoGen
      spaceInside <- Gen.stringOf(Gen.const(' '))
      spaceIndex  <- Gen.choose(1, nino.length - 1)
    } yield {
      val (beginning, end) = nino.splitAt(spaceIndex)
      s"$spaceBefore$beginning$spaceInside$end$spaceAfter"
    }
    val gen               = Gen.oneOf(ninoGen, ninoWithSpacesGen)

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      gen
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
    "Be accepted only if it is in the right format" in {
      val result = form.bind(Map(fieldName -> "GB123456A")).apply(fieldName)
      result.errors.head mustBe FormError(fieldName, "pensionSchemeMemberNino.error.invalid")
    }

  }

}
