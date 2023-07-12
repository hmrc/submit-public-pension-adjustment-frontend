package forms

import javax.inject.Inject

import forms.mappings.Mappings
import play.api.data.Form
import models.WhichPensionSchemeWillPayTaxRelief

class WhichPensionSchemeWillPayTaxReliefFormProvider @Inject() extends Mappings {

  def apply(): Form[WhichPensionSchemeWillPayTaxRelief] =
    Form(
      "value" -> enumerable[WhichPensionSchemeWillPayTaxRelief]("whichPensionSchemeWillPayTaxRelief.error.required")
    )
}
