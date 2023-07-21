package forms

import java.time.LocalDate

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class MemberDateOfDeathFormProvider @Inject() extends Mappings {

  def apply(): Form[LocalDate] =
    Form(
      "value" -> localDate(
        invalidKey     = "memberDateOfDeath.error.invalid",
        allRequiredKey = "memberDateOfDeath.error.required.all",
        twoRequiredKey = "memberDateOfDeath.error.required.two",
        requiredKey    = "memberDateOfDeath.error.required"
      )
    )
}
