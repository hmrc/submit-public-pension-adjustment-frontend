package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class ClaimingHigherOrAdditionalTaxRateReliefFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "claimingHigherOrAdditionalTaxRateRelief.error.required"
  val invalidKey = "error.boolean"

  val form = new ClaimingHigherOrAdditionalTaxRateReliefFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
