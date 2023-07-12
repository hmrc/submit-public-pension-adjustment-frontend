package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait WhichPensionSchemeWillPayTaxRelief

object WhichPensionSchemeWillPayTaxRelief extends Enumerable.Implicits {

  case object Pensionschemea extends WithName("pensionSchemeA") with WhichPensionSchemeWillPayTaxRelief
  case object Pensionschemeb extends WithName("pensionSchemeB") with WhichPensionSchemeWillPayTaxRelief

  val values: Seq[WhichPensionSchemeWillPayTaxRelief] = Seq(
    Pensionschemea,
    Pensionschemeb
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map { case (value, index) =>
    RadioItem(
      content = Text(messages(s"whichPensionSchemeWillPayTaxRelief.${value.toString}")),
      value = Some(value.toString),
      id = Some(s"value_$index")
    )
  }

  implicit val enumerable: Enumerable[WhichPensionSchemeWillPayTaxRelief] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
