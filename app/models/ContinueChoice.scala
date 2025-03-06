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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.hint.Hint
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait ContinueChoice

object ContinueChoice extends Enumerable.Implicits {

  case object Continue extends WithName("continue") with ContinueChoice
  case object Edit extends WithName("edit") with ContinueChoice
  case object Restart extends WithName("restart") with ContinueChoice

  val values: Seq[ContinueChoice] = Seq(
    Continue,
    Edit,
    Restart
  )

  def options(implicit messages: Messages): Seq[RadioItem] = values.zipWithIndex.map { case (value, index) =>
    RadioItem(
      content = Text(messages(s"continueChoice.${value.toString}")),
      hint = Option.apply(Hint(content = HtmlContent(messages(s"continueChoice.${value.toString}.hint")))),
      value = Some(value.toString),
      id = Some(s"value_$index")
    )
  }

  implicit val enumerable: Enumerable[ContinueChoice] =
    Enumerable(values.map(v => v.toString -> v)*)
}
