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

package generators

import org.scalacheck.Arbitrary.{arbString, _}
import org.scalacheck.Gen._
import org.scalacheck.{Gen, Shrink}

import java.time.{Instant, LocalDate, ZoneOffset}

trait Generators extends UserAnswersGenerator with PageGenerators with ModelGenerators with UserAnswersEntryGenerators {

  implicit val dontShrink: Shrink[String] = Shrink.shrinkAny

  def genIntersperseString(gen: Gen[String], value: String, frequencyV: Int = 1, frequencyN: Int = 10): Gen[String] = {

    val genValue: Gen[Option[String]] = Gen.frequency(frequencyN -> None, frequencyV -> Gen.const(Some(value)))

    for {
      seq1 <- gen
      seq2 <- Gen.listOfN(seq1.length, genValue)
    } yield seq1.toSeq.zip(seq2).foldLeft("") {
      case (acc, (n, Some(v))) =>
        acc + n + v
      case (acc, (n, _))       =>
        acc + n
    }
  }

  def validPstrs: Gen[String] = {
    val pstr = for {
      firstDigits <- Gen.listOfN(8, Gen.numChar).map(_.mkString)
      secondChars <- Gen.listOfN(2, Gen.alphaChar).map(_.mkString)
    } yield s"$firstDigits $secondChars".toUpperCase.replaceAll(" ", "")
    if (pstr.toString == "00348916RT") {
      validPstrs
    } else {
      pstr
    }
  }

  def intsInRangeWithCommas(min: Int, max: Int): Gen[String] = {
    val numberGen = choose[Int](min, max).map(_.toString)
    genIntersperseString(numberGen, ",")
  }

  def intsLargerThanMaxValue: Gen[BigInt] =
    arbitrary[BigInt] suchThat (x => x > Int.MaxValue)

  def intsSmallerThanMinValue: Gen[BigInt] =
    arbitrary[BigInt] suchThat (x => x < Int.MinValue)

  def nonNumerics: Gen[String] =
    alphaStr suchThat (_.size > 0)

  def decimals: Gen[String] =
    arbitrary[BigDecimal]
      .suchThat(_.abs < Int.MaxValue)
      .suchThat(!_.isValidInt)
      .map(value => "%f".format(value))

  def intsBelowValue(value: Int): Gen[Int] =
    arbitrary[Int] suchThat (_ < value)

  def intsAboveValue(value: Int): Gen[Int] =
    arbitrary[Int] suchThat (_ > value)

  def intsOutsideRange(min: Int, max: Int): Gen[Int] =
    arbitrary[Int] suchThat (x => x < min || x > max)

  def nonBooleans: Gen[String] =
    arbitrary[String]
      .suchThat(_.nonEmpty)
      .suchThat(_ != "true")
      .suchThat(_ != "false")

  def nonEmptyString: Gen[String] =
    arbitrary[String] suchThat (_.nonEmpty)

  def stringsWithMaxLength(maxLength: Int): Gen[String] =
    for {
      length <- choose(1, maxLength)
      chars  <- listOfN(length, arbitrary[Char])
    } yield chars.mkString

  def stringsLongerThan(minLength: Int): Gen[String] = for {
    maxLength <- (minLength * 2).max(100)
    length    <- Gen.chooseNum(minLength + 1, maxLength)
    chars     <- listOfN(length, arbitrary[Char])
  } yield chars.mkString

  def stringsExceptSpecificValues(excluded: Seq[String]): Gen[String] =
    nonEmptyString suchThat (!excluded.contains(_))

  def oneOf[T](xs: Seq[Gen[T]]): Gen[T] =
    if (xs.isEmpty) {
      throw new IllegalArgumentException("oneOf called on empty collection")
    } else {
      val vector = xs.toVector
      choose(0, vector.size - 1).flatMap(vector(_))
    }

  def datesBetween(min: LocalDate, max: LocalDate): Gen[LocalDate] = {

    def toMillis(date: LocalDate): Long =
      date.atStartOfDay.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map { millis =>
      Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate
    }
  }

  def stringsOfLength(length: Int, charGen: Gen[Char] = arbitrary[Char]): Gen[String] = for {
    chars <- listOfN(length, charGen)
  } yield chars.mkString

  def intsOfLengthAsString(length: Int): Gen[String] =
    Gen.listOfN(length, Gen.numChar).map(_.mkString)

  def unsafeInputs: Gen[Char] = Gen.oneOf(
    Gen.const('<'),
    Gen.const('>'),
    Gen.const('='),
    Gen.const('|')
  )

  def unsafeInputsWithMaxLength(maxLength: Int): Gen[String] = (for {
    length      <- choose(2, maxLength)
    invalidChar <- unsafeInputs
    validChars  <- listOfN(length - 1, unsafeInputs)
  } yield (validChars :+ invalidChar).mkString).suchThat(_.trim.nonEmpty)

  def validAccountName: Gen[String] = {
    val allowedChars =
      Gen.oneOf(('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ Seq('&', '`', '-', '\'', '.', '^'))
    for {
      firstPartLength  <- Gen.choose(1, 34)
      secondPartLength <- Gen.choose(1, 35)
      firstPart        <- Gen.listOfN(firstPartLength, allowedChars)
      secondPart       <- Gen.listOfN(secondPartLength, allowedChars)
    } yield (firstPart.mkString + " " + secondPart.mkString).take(70)
  }

  def validSortCode: Gen[String] = for {
    firstPair  <- Gen.listOfN(2, Gen.numChar).map(_.mkString)
    secondPair <- Gen.listOfN(2, Gen.numChar).map(_.mkString)
    thirdPair  <- Gen.listOfN(2, Gen.numChar).map(_.mkString)
    separator  <- Gen.oneOf("-", " ", "")
  } yield s"$firstPair$separator$secondPair$separator$thirdPair"

  def validAccountNumber: Gen[String] = Gen.listOfN(8, Gen.numChar).map(_.mkString)
}
