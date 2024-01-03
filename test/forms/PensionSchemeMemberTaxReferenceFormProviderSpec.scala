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

package forms

import forms.behaviours.StringFieldBehaviours
import org.scalacheck.Gen
import play.api.data.FormError

class PensionSchemeMemberTaxReferenceFormProviderSpec extends StringFieldBehaviours {

  val form = new PensionSchemeMemberTaxReferenceFormProvider()()

  val invalidKey = "pensionSchemeMemberTaxReference.error.invalid"
  val lengthKey  = "pensionSchemeMemberTaxReference.error.length"
  val maxLength  = 10

  val validAnswer = Gen.listOfN(10, Gen.numChar).map(_.mkString)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validAnswer
    )

    behave like fieldThatDoesNotBindInvalidStrings(
      form = form,
      fieldName = fieldName,
      regex = """^(?!1234567890)\d{10}$""",
      gen = stringsOfLength(maxLength),
      invalidKey = invalidKey
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )
  }
}
