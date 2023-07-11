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

import play.api.mvc.QueryStringBindable

import scala.util.matching.Regex

case class UniqueId(value: String) {
  override def toString: String = value.toString
}

object UniqueId {

  val pattern: Regex =
    "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$".r.anchored

  implicit def uniqueIdBindable(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[UniqueId] =
    new QueryStringBindable[UniqueId] {

      override def unbind(key: String, index: UniqueId): String =
        stringBinder.unbind(key, index.value)

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, UniqueId]] =
        params.get(key) match {
          case Some(Seq(value)) => fromString(value)
          case _                => Some(Left("invalid param"))
        }
    }

  def fromString(uuidString: String): Option[Either[String, UniqueId]] =
    if (pattern.matches(uuidString)) Some(Right(UniqueId(uuidString))) else Some(Left("invalid param format"))
}
