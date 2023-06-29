package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.FormError

class PensionSchemeMemberNameFormProviderSpec extends StringFieldBehaviours {

  val requiredKey = "pensionSchemeMemberName.error.required"
  val lengthKey = "pensionSchemeMemberName.error.length"
  val maxLength = 30

  val form = new PensionSchemeMemberNameFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
