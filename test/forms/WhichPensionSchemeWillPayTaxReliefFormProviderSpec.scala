package forms

import forms.behaviours.OptionFieldBehaviours
import models.WhichPensionSchemeWillPayTaxRelief
import play.api.data.FormError

class WhichPensionSchemeWillPayTaxReliefFormProviderSpec extends OptionFieldBehaviours {

  val form = new WhichPensionSchemeWillPayTaxReliefFormProvider()()

  ".value" - {

    val fieldName   = "value"
    val requiredKey = "whichPensionSchemeWillPayTaxRelief.error.required"

    behave like optionsField[WhichPensionSchemeWillPayTaxRelief](
      form,
      fieldName,
      validValues = WhichPensionSchemeWillPayTaxRelief.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
