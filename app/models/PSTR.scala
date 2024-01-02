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

import play.api.libs.json.{Format, Json}
import play.api.mvc.PathBindable

import scala.util.matching.Regex

case class PSTR(value: String)

object PSTR {

  implicit lazy val formats: Format[PSTR] = Json.format

  val New: String = "Private pension scheme"

  private val pattern: Regex = """(\d{8})[A-Z]{2}""".r.anchored

  def fromString(pstrString: String): Option[PSTR] =
    pstrString match {
      case "00348916RT" => None
      case pattern(_)   => Some(PSTR(pstrString))
      case _            => None
    }

  implicit def pstrPathBindable(implicit stringBinder: PathBindable[String]): PathBindable[PSTR] =
    new PathBindable[PSTR] {

      override def bind(key: String, indexString: String): Either[String, PSTR] =
        fromString(indexString) match {
          case Some(schemeIndex: PSTR) => Right(schemeIndex)
          case None                    => Left("Invalid pstr")
        }

      override def unbind(key: String, index: PSTR): String =
        stringBinder.unbind(key, index.value)
    }

}
