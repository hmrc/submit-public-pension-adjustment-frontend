package forms

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class HowMuchTaxReliefFormProvider @Inject() extends Mappings {

  def apply(): Form[Int] =
    Form(
      "value" -> int(
        "howMuchTaxRelief.error.required",
        "howMuchTaxRelief.error.wholeNumber",
        "howMuchTaxRelief.error.nonNumeric")
          .verifying(inRange(0, Int.MaxValue, "howMuchTaxRelief.error.outOfRange"))
    )
}
