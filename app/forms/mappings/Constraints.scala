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

package forms.mappings

import java.time.LocalDate
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

trait Constraints {
  private val validAccountNameRegex: String = """^[a-z0-9A-Z &`\-'.\^]*$"""
  private val validSortCodeRegex: String    = "^[0-9]{2}[-\\s]?[0-9]{2}[-\\s]?[0-9]{2}$"

  protected def firstError[A](constraints: Constraint[A]*): Constraint[A] =
    Constraint { input =>
      constraints
        .map(_.apply(input))
        .find(_ != Valid)
        .getOrElse(Valid)
    }

  protected def minimumValue[A](minimum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint { input =>
      import ev._

      if (input >= minimum) {
        Valid
      } else {
        Invalid(errorKey, minimum)
      }
    }

  protected def maximumValue[A](maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint { input =>
      import ev._

      if (input <= maximum) {
        Valid
      } else {
        Invalid(errorKey, maximum)
      }
    }

  protected def inRange[A](minimum: A, maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint { input =>
      import ev._

      if (input >= minimum && input <= maximum) {
        Valid
      } else {
        Invalid(errorKey, minimum, maximum)
      }
    }

  protected def regexp(regex: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.matches(regex) =>
        Valid
      case _                         =>
        Invalid(errorKey, regex)
    }

  protected def maxLength(maximum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case _                            =>
        Invalid(errorKey, maximum)
    }

  protected def maxDate(maximum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isAfter(maximum) =>
        Invalid(errorKey, args: _*)
      case _                             =>
        Valid
    }

  protected def minDate(minimum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isBefore(minimum) =>
        Invalid(errorKey, args: _*)
      case _                              =>
        Valid
    }

  protected def nonEmptySet(errorKey: String): Constraint[Set[_]] =
    Constraint {
      case set if set.nonEmpty =>
        Valid
      case _                   =>
        Invalid(errorKey)
    }

  def accountNameFormatConstraint: Constraint[String] = Constraint[String]("constraint.accountname.format") { an =>
    if (an.length > 70) Invalid(ValidationError("bankDetails.invalid.account.name.length"))
    else if (an.isEmpty) Invalid(ValidationError("bankDetails.invalid.account.name.empty"))
    else if (!an.matches(validAccountNameRegex)) Invalid(ValidationError("bankDetails.invalid.account.name"))
    else Valid
  }

  def sortCodeFormatConstraint: Constraint[String] = Constraint[String]("constraint.sortcode.format") { sc =>
    if (sc.count(_.isDigit) == 6 && sc.matches(validSortCodeRegex))
      Valid
    else
      Invalid(ValidationError("bankDetails.invalid.sortcode"))
  }

  def accountNumberFormatConstraint: Constraint[String] = Constraint[String]("constraint.accountnumber.format") { an =>
    if (an.length == 8 && an.forall(_.isDigit))
      Valid
    else
      Invalid(ValidationError("bankDetails.invalid.account.number"))
  }
}
