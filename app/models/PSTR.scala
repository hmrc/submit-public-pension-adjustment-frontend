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

package models

import scala.util.matching.Regex

case class PSTR(value: String)

object PSTR {

  val New: String = "New"

  private val pattern: Regex = """(\d{8})[A-Z]{2}""".r.anchored

  def fromString(pstrString: String): Option[PSTR] =
    pstrString match {
      case pattern(_) => Some(PSTR(pstrString))
      case _          => None
    }
}
