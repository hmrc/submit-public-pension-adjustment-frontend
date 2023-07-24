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

package models.calculation.response

import play.api.libs.json.{Reads, Writes, __}

case class Resubmission(isResubmission: Boolean, reason: Option[String])

object Resubmission {

  implicit lazy val reads: Reads[Resubmission] = {
    import play.api.libs.functional.syntax._

    ((__ \ "isResubmission").read[Boolean] and
      (__ \ "reason").readNullable[String])(Resubmission(_, _))

  }

  implicit lazy val writes: Writes[Resubmission] = {
    import play.api.libs.functional.syntax._

    ((__ \ "isResubmission").write[Boolean] and
      (__ \ "reason").writeNullable[String])(a => (a.isResubmission, a.reason))

  }

}
