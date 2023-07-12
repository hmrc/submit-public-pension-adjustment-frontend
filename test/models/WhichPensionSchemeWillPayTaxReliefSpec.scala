package models

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class WhichPensionSchemeWillPayTaxReliefSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with OptionValues {

  "WhichPensionSchemeWillPayTaxRelief" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(WhichPensionSchemeWillPayTaxRelief.values.toSeq)

      forAll(gen) { whichPensionSchemeWillPayTaxRelief =>
        JsString(whichPensionSchemeWillPayTaxRelief.toString)
          .validate[WhichPensionSchemeWillPayTaxRelief]
          .asOpt
          .value mustEqual whichPensionSchemeWillPayTaxRelief
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!WhichPensionSchemeWillPayTaxRelief.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[WhichPensionSchemeWillPayTaxRelief] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(WhichPensionSchemeWillPayTaxRelief.values.toSeq)

      forAll(gen) { whichPensionSchemeWillPayTaxRelief =>
        Json.toJson(whichPensionSchemeWillPayTaxRelief) mustEqual JsString(whichPensionSchemeWillPayTaxRelief.toString)
      }
    }
  }
}
