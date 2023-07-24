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

package models.submission

import models.calculation.inputs.CalculationInputs
import models.calculation.response.CalculationResponse
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Format, Reads, Writes, __}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

case class Submission(
  sessionId: String,
  uniqueId: String,
  calculationInputs: CalculationInputs,
  calculation: Option[CalculationResponse],
  lastUpdated: Instant = Instant.now
)

object Submission {

  val reads: Reads[Submission] =
    (
      (__ \ "sessionId").read[String] and
        (__ \ "uniqueId").read[String] and
        (__ \ "calculationInputs").read[CalculationInputs] and
        (__ \ "calculation").readNullable[CalculationResponse] and
        (__ \ "lastUpdated").read(MongoJavatimeFormats.instantFormat)
    )(Submission.apply _)

  val writes: Writes[Submission] =
    (
      (__ \ "sessionId").write[String] and
        (__ \ "uniqueId").write[String] and
        (__ \ "calculationInputs").write[CalculationInputs] and
        (__ \ "calculation").writeNullable[CalculationResponse] and
        (__ \ "lastUpdated").write(MongoJavatimeFormats.instantFormat)
    )(unlift(Submission.unapply))

  implicit val format: Format[Submission] = Format(reads, writes)
}
