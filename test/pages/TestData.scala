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

package pages

import models.calculation.inputs.Income.{AboveThreshold, BelowThreshold}
import models.calculation.inputs.TaxYear2016To2023.{NormalTaxYear, PostFlexiblyAccessedTaxYear}
import models.calculation.inputs.*
import models.calculation.response.{CalculationResponse, InDatesTaxYearSchemeCalculation, InDatesTaxYearsCalculation, OutOfDatesTaxYearSchemeCalculation, OutOfDatesTaxYearsCalculation, Period => responsePeriod, Resubmission => responseResubmission, TaxYearScheme, TotalAmounts}
import models.finalsubmission.OnBehalfOfMemberType.PowerOfAttorney
import models.finalsubmission.*
import models.{InternationalAddress, PSTR, UkAddress, UserAnswers}
import play.api.libs.json.{JsObject, Json}

import java.time.LocalDate

object TestData {

  val lifeTimeAllowanceWithMultipleSchemes =
    lifeTimeAllowance(Some(WhoPayingExtraLtaCharge.You), Some(LtaPensionSchemeDetails("Scheme2", "pstr2")))

  val lifeTimeAllowanceWithSingeScheme = lifeTimeAllowance(None, None)

  private def lifeTimeAllowance(
    whoPayingExtra: Option[WhoPayingExtraLtaCharge],
    pensionSchemeDetails: Option[LtaPensionSchemeDetails]
  ) = LifeTimeAllowance(
    benefitCrystallisationEventDate = LocalDate.of(2017, 1, 30),
    lifetimeAllowanceProtectionOrEnhancements = LtaProtectionOrEnhancements.Protection,
    protectionType = Some(ProtectionType.PrimaryProtection),
    protectionReference = Some("originalReference"),
    ProtectionEnhancedChanged.Protection,
    newProtectionTypeOrEnhancement = Some(WhatNewProtectionTypeEnhancement.EnhancedProtection),
    newProtectionTypeOrEnhancementReference = Some("newReference"),
    previousLifetimeAllowanceChargeFlag = true,
    previousLifetimeAllowanceChargePaymentMethod = Some(ExcessLifetimeAllowancePaid.Lumpsum),
    previousLifetimeAllowanceChargePaidBy = Some(WhoPaidLTACharge.You),
    previousLifetimeAllowanceChargeSchemeNameAndTaxRef = Some(SchemeNameAndTaxRef("Scheme1", "pstr1")),
    newLifetimeAllowanceChargeWillBePaidBy = whoPayingExtra,
    newLifetimeAllowanceChargeSchemeNameAndTaxRef = pensionSchemeDetails,
    NewLifeTimeAllowanceAdditions(
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None
    )
  )

  val authRetrievals =
    AuthRetrievals(
      "AA000000A",
      "Test User",
      Some("1234567890"),
      Some(LocalDate.of(1968, 11, 30))
    )

  val incomeSubJourney =
    IncomeSubJourney(
      Some(1211),
      Some(1618),
      Some(3345),
      Some(948),
      Some(true),
      Some(519),
      Some(2189),
      Some(8181),
      Some(4999),
      Some(3474),
      Some(589),
      Some(4573),
      Some(90),
      Some(2291),
      None,
      None
    )

  val calculationInputs = CalculationInputs(
    Resubmission(false, None),
    Setup(
      Some(
        AnnualAllowanceSetup(
          Some(true),
          Some(false),
          Some(false),
          Some(false),
          Some(false),
          Some(false),
          Some(MaybePIAIncrease.No),
          Some(MaybePIAUnchangedOrDecreased.No),
          Some(false),
          Some(false),
          Some(false),
          Some(false)
        )
      ),
      Some(
        LifetimeAllowanceSetup(
          Some(true),
          Some(false),
          Some(true),
          Some(false),
          Some(false),
          Some(false),
          Some(true)
        )
      )
    ),
    Some(
      AnnualAllowance(
        List(Period._2021, Period._2019),
        List(
          TaxYear2011To2015(20000, Period._2011),
          TaxYear2011To2015(50000, Period._2013),
          TaxYear2011To2015(50000, Period._2014),
          TaxYear2011To2015(40000, Period._2015),
          PostFlexiblyAccessedTaxYear(
            0,
            20000,
            60000,
            0,
            List(TaxYearScheme("Scheme 1", "00348916RT", 20000, 0, Some(36000))),
            Period._2016,
            incomeSubJourney,
            None
          ),
          NormalTaxYear(
            45000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 45000, 0, None)),
            60000,
            0,
            Period._2017,
            incomeSubJourney,
            Some(BelowThreshold)
          ),
          NormalTaxYear(
            38000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 38000, 200, None)),
            60000,
            200,
            Period._2018,
            incomeSubJourney,
            Some(BelowThreshold)
          ),
          NormalTaxYear(
            43000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 43000, 0, None)),
            60000,
            3280,
            Period._2019,
            incomeSubJourney,
            Some(BelowThreshold)
          ),
          NormalTaxYear(
            45000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 45000, 9000, None)),
            180000,
            0,
            Period._2020,
            incomeSubJourney,
            Some(AboveThreshold(160000))
          ),
          NormalTaxYear(
            42000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 42000, 0, None)),
            150000,
            0,
            Period._2021,
            incomeSubJourney,
            Some(BelowThreshold)
          ),
          NormalTaxYear(
            43500,
            List(TaxYearScheme("Scheme 1", "00348916RT", 43500, 2400, None)),
            60000,
            0,
            Period._2022,
            incomeSubJourney,
            Some(BelowThreshold)
          ),
          NormalTaxYear(
            42000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 42000, 0, None)),
            60000,
            0,
            Period._2023,
            incomeSubJourney,
            Some(BelowThreshold)
          )
        )
      )
    ),
    Some(
      LifeTimeAllowance(
        LocalDate.parse("2018-10-27"),
        LtaProtectionOrEnhancements.Protection,
        Some(ProtectionType.FixedProtection2014),
        Some("R41AB678TR23355"),
        ProtectionEnhancedChanged.Protection,
        Some(WhatNewProtectionTypeEnhancement.IndividualProtection2014),
        Some("2134567801"),
        true,
        Some(ExcessLifetimeAllowancePaid.Annualpayment),
        Some(WhoPaidLTACharge.PensionScheme),
        Some(SchemeNameAndTaxRef("Scheme 1", "00348916RT")),
        Some(WhoPayingExtraLtaCharge.You),
        None,
        NewLifeTimeAllowanceAdditions(
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None
        )
      )
    )
  )

  val calculationInputsLTAOnly: CalculationInputs = CalculationInputs(
    Resubmission(false, None),
    Setup(
      None,
      Some(
        LifetimeAllowanceSetup(
          Some(true),
          Some(false),
          Some(true),
          Some(false),
          Some(false),
          Some(false),
          Some(true)
        )
      )
    ),
    None,
    Some(
      LifeTimeAllowance(
        LocalDate.parse("2018-10-27"),
        LtaProtectionOrEnhancements.Protection,
        Some(ProtectionType.FixedProtection2014),
        Some("R41AB678TR23355"),
        ProtectionEnhancedChanged.Protection,
        Some(WhatNewProtectionTypeEnhancement.IndividualProtection2014),
        Some("2134567801"),
        true,
        Some(ExcessLifetimeAllowancePaid.Annualpayment),
        Some(WhoPaidLTACharge.PensionScheme),
        Some(SchemeNameAndTaxRef("Scheme 1", "00348916RT")),
        Some(WhoPayingExtraLtaCharge.You),
        None,
        NewLifeTimeAllowanceAdditions(
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None
        )
      )
    )
  )

  val calculation = Some(
    CalculationResponse(
      responseResubmission(false, None),
      TotalAmounts(10470, 1620, 5500),
      List(
        OutOfDatesTaxYearsCalculation(
          responsePeriod._2016,
          0,
          0,
          0,
          0,
          0,
          0,
          40000,
          List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0)),
          Some(0)
        ),
        OutOfDatesTaxYearsCalculation(
          responsePeriod._2017,
          0,
          0,
          0,
          0,
          5000,
          2000,
          0,
          List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0)),
          Some(0)
        ),
        OutOfDatesTaxYearsCalculation(
          responsePeriod._2018,
          200,
          200,
          200,
          200,
          0,
          0,
          2000,
          List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 200)),
          Some(0)
        ),
        OutOfDatesTaxYearsCalculation(
          responsePeriod._2019,
          2870,
          0,
          3280,
          0,
          1000,
          410,
          0,
          List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0)),
          Some(0)
        )
      ),
      List(
        InDatesTaxYearsCalculation(
          responsePeriod._2020,
          0,
          4500,
          0,
          0,
          9000,
          10000,
          4500,
          0,
          List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 9000)),
          Some(0)
        ),
        InDatesTaxYearsCalculation(
          responsePeriod._2021,
          0,
          0,
          820,
          0,
          0,
          2000,
          820,
          0,
          List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0)),
          Some(0)
        ),
        InDatesTaxYearsCalculation(
          responsePeriod._2022,
          0,
          1000,
          300,
          0,
          2400,
          3500,
          1400,
          0,
          List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 2400)),
          Some(0)
        ),
        InDatesTaxYearsCalculation(
          responsePeriod._2023,
          0,
          0,
          800,
          0,
          0,
          2000,
          800,
          0,
          List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0)),
          Some(0)
        )
      )
    )
  )

  val userAnswerData1 = Json
    .parse("""
      |{
      |  "claimOnBehalf": false,
      |  "aa": {
      |    "years": {
      |      "2021": {
      |        "whoWillPay": "pensionScheme",
      |        "whichPensionSchemeWillPay": "Scheme 1 / 00348916RT",
      |        "askedPensionSchemeToPayTaxCharge": true,
      |        "whenDidYouAskPensionSchemeToPay": "2021-07-20"
      |      },
      |     "2022": {
      |        "whoWillPay": "you"
      |      },
      |      "2023": {
      |        "whoWillPay": "pensionScheme",
      |        "whichPensionSchemeWillPay": "Private pension scheme",
      |        "pensionSchemeDetails": {
      |          "pensionSchemeName": "Scheme 2",
      |          "pensionSchemeTaxReference": "00348916NQ"
      |        },
      |        "askedPensionSchemeToPayTaxCharge": false,
      |        "whenWillYouAskPensionSchemeToPay": "octToDec24"
      |      }
      |    },
      |    "schemes": {
      |      "00348916RT": {
      |        "legacyPensionSchemeReference": "AB189416C",
      |        "reformPensionSchemeReference": "R41AB678TR23355"
      |      }
      |    }
      |  },
      |  "alternativeName": true,
      |  "contactNumber": "01632 960 001",
      |  "areYouAUKResident": true,
      |  "ukAddress": {
      |    "addressLine1": "Test Address line 1",
      |    "townOrCity": "Test city",
      |    "county": "Test county",
      |    "postCode": "AB1 2CD"
      |  },
      |  "claimingHigherOrAdditionalTaxRateRelief": true,
      |  "howMuchTaxRelief": 10000,
      |  "whichPensionSchemeWillPayTaxRelief": "Scheme 1 / 00348916RT"
      |}
      |""".stripMargin)
    .as[JsObject]

  val userAnswers1 = UserAnswers(
    "Test UserId",
    userAnswerData1
  )

  val userAnswerData2 = Json
    .parse("""
      |{
      |  "claimOnBehalf": true,
      |  "statusOfUser": "powerOfAttorney",
      |  "pensionSchemeMemberName": "Test SchemeUser",
      |  "pensionSchemeMemberDOB": "1962-10-24",
      |  "pensionSchemeMemberNino": "AB123456C",
      |  "pensionSchemeMemberTaxReference": "1234567890",
      |  "pensionSchemeMemberResidence": false,
      |  "pensionSchemeMemberInternationalAddress": {
      |    "addressLine1": "Test Address line 1",
      |    "addressLine2": "Test Address line 2",
      |    "townOrCity": "Test city",
      |    "postCode": "NP4 9KL",
      |    "country": "Spain"
      |  },
      |  "alternativeName": false,
      |  "enterAlternativeName": "Duplicate Name",
      |  "contactNumber": "01632 960 001",
      |  "areYouAUKResident": true,
      |  "ukAddress": {
      |    "addressLine1": "Test UK Address line 1",
      |    "townOrCity": "Test UK city",
      |    "postCode": "AB1 2CD"
      |  },
      |  "aa": {
      |    "schemes": {
      |      "00348916RT": {
      |        "legacyPensionSchemeReference": "R41AB678TR23355",
      |        "reformPensionSchemeReference": "AB189416C"
      |      }
      |    }
      |  },
      |  "claimingHigherOrAdditionalTaxRateRelief": true,
      |  "howMuchTaxRelief": 10000,
      |  "whichPensionSchemeWillPayTaxRelief": "Scheme 1 / 00348916RT",
      |  "schemeCreditConsent": true
      |}
      |""".stripMargin)
    .as[JsObject]

  val userAnswers2 = UserAnswers(
    "Test UserId",
    userAnswerData2
  )

  val userAnswerData3 = Json
    .parse("""
             |{
             |  "claimOnBehalf": true,
             |  "statusOfUser": "deputyship",
             |  "pensionSchemeMemberName": "Test SchemeUser",
             |  "pensionSchemeMemberDOB": "1962-10-24",
             |  "pensionSchemeMemberNino": "AB123456C",
             |  "pensionSchemeMemberTaxReference": "1234567890",
             |  "pensionSchemeMemberResidence": false,
             |  "pensionSchemeMemberInternationalAddress": {
             |    "addressLine1": "Test Address line 1",
             |    "addressLine2": "Test Address line 2",
             |    "townOrCity": "Test city",
             |    "postCode": "NP4 9KL",
             |    "country": "Spain"
             |  },
             |  "alternativeName": false,
             |  "enterAlternativeName": "Duplicate Name",
             |  "contactNumber": "01632 960 001",
             |  "areYouAUKResident": true,
             |  "ukAddress": {
             |    "addressLine1": "Test UK Address line 1",
             |    "townOrCity": "Test UK city",
             |    "postCode": "AB1 2CD"
             |  },
             |  "bankDetails" : {
             |    "accountName": "TestFName TestLName",
             |    "sortCode": "012345",
             |    "accountNumber": "12345678",
             |    "rollNumber": "1222"
             |  },
             |  "aa": {
             |    "schemes": {
             |      "00348916RT": {
             |        "legacyPensionSchemeReference": "R41AB678TR23355",
             |        "reformPensionSchemeReference": "AB189416C"
             |      }
             |    }
             |  },
             |  "claimingHigherOrAdditionalTaxRateRelief": true,
             |  "howMuchTaxRelief": 10000,
             |  "whichPensionSchemeWillPayTaxRelief": "Scheme 1 / 00348916RT"
             |}
             |""".stripMargin)
    .as[JsObject]

  val userAnswers3 = UserAnswers(
    "Test UserId",
    userAnswerData3
  )

  val userAnswerData4 = Json
    .parse("""
             |{
             |  "claimOnBehalf": true,
             |  "statusOfUser": "legalPersonalRepresentative",
             |  "pensionSchemeMemberName": "Test SchemeUser",
             |  "pensionSchemeMemberDOB": "1962-10-24",
             |  "pensionSchemeMemberNino": "AB123456C",
             |  "pensionSchemeMemberTaxReference": "1234567890",
             |  "pensionSchemeMemberResidence": false,
             |  "pensionSchemeMemberInternationalAddress": {
             |    "addressLine1": "Test Address line 1",
             |    "addressLine2": "Test Address line 2",
             |    "townOrCity": "Test city",
             |    "postCode": "NP4 9KL",
             |    "country": "Spain"
             |  },
             |  "alternativeName": false,
             |  "enterAlternativeName": "Duplicate Name",
             |  "contactNumber": "01632 960 001",
             |  "areYouAUKResident": true,
             |  "ukAddress": {
             |    "addressLine1": "Test UK Address line 1",
             |    "townOrCity": "Test UK city",
             |    "postCode": "AB1 2CD"
             |  },
             |  "bankDetails" : {
             |    "accountName": "TestFName TestLName",
             |    "sortCode": "012345",
             |    "accountNumber": "12345678"
             |  },
             |  "aa": {
             |    "schemes": {
             |      "00348916RT": {
             |        "legacyPensionSchemeReference": "R41AB678TR23355",
             |        "reformPensionSchemeReference": "AB189416C"
             |      }
             |    }
             |  },
             |  "claimingHigherOrAdditionalTaxRateRelief": true,
             |  "howMuchTaxRelief": 10000,
             |  "whichPensionSchemeWillPayTaxRelief": "Scheme 1 / 00348916RT"
             |}
             |""".stripMargin)
    .as[JsObject]

  val userAnswers4 = UserAnswers(
    "Test UserId",
    userAnswerData4
  )

  val submissionInputs1 = SubmissionInputs(
    AdministrativeDetails(
      ClaimantDetails(
        PersonalDetails(
          "Test User",
          None,
          Some(LocalDate.of(1968, 11, 30)),
          Some(
            UkAddress(None, "Test Address line 1", None, None, "Test city", Some("Test county"), Some("AB1 2CD"), None)
          ),
          None,
          None,
          None,
          Some("01632 960 001")
        ),
        TaxIdentifiers(Some("AA000000A"), None, Some("1234567890"))
      ),
      None
    ),
    List(
      PaymentElection(
        models.calculation.inputs.Period._2021,
        None,
        Some(
          SchemeCharge(820, SchemeDetails("Scheme 1", PSTR("00348916RT")), Some(LocalDate.parse("2021-07-20")))
        )
      ),
      PaymentElection(
        models.calculation.inputs.Period._2022,
        Some(PersonalCharge(300)),
        None
      ),
      PaymentElection(
        models.calculation.inputs.Period._2023,
        None,
        Some(
          SchemeCharge(
            800,
            SchemeDetails("Scheme 2", PSTR("00348916NQ")),
            None
          )
        )
      )
    ),
    List(
      IndividualSchemeIdentifier(
        SchemeDetails("Scheme 1", PSTR("00348916RT")),
        Some("AB189416C"),
        Some("R41AB678TR23355")
      )
    ),
    Some(
      SchemeTaxRelief(
        10000,
        IndividualSchemeIdentifier(
          SchemeDetails("Scheme 1", PSTR("00348916RT")),
          Some("AB189416C"),
          Some("R41AB678TR23355")
        )
      )
    ),
    None,
    Declarations(true, true, true, None, None, None, None)
  )

  val submissionInputs2 = SubmissionInputs(
    AdministrativeDetails(
      ClaimantDetails(
        PersonalDetails(
          "Test User",
          Some("Duplicate Name"),
          Some(LocalDate.of(1968, 11, 30)),
          Some(UkAddress(None, "Test UK Address line 1", None, None, "Test UK city", None, Some("AB1 2CD"), None)),
          None,
          None,
          None,
          Some("01632 960 001")
        ),
        TaxIdentifiers(Some("AA000000A"), None, Some("1234567890"))
      ),
      Some(
        OnBehalfOfMember(
          PersonalDetails(
            "Test SchemeUser",
            Some("Duplicate Name"),
            Some(LocalDate.of(1962, 10, 24)),
            None,
            None,
            None,
            Some(
              InternationalAddress(
                None,
                "Test Address line 1",
                Some("Test Address line 2"),
                None,
                "Test city",
                None,
                Some("NP4 9KL"),
                "Spain"
              )
            ),
            Some("01632 960 001")
          ),
          TaxIdentifiers(Some("AB123456C"), None, Some("1234567890")),
          None,
          PowerOfAttorney
        )
      )
    ),
    List(
      PaymentElection(Period._2021, None, None),
      PaymentElection(Period._2022, None, None),
      PaymentElection(Period._2023, None, None)
    ),
    List(
      IndividualSchemeIdentifier(
        SchemeDetails("Scheme 1", PSTR("00348916RT")),
        Some("R41AB678TR23355"),
        Some("AB189416C")
      )
    ),
    Some(
      SchemeTaxRelief(
        10000,
        IndividualSchemeIdentifier(
          SchemeDetails("Scheme 1", PSTR("00348916RT")),
          Some("R41AB678TR23355"),
          Some("AB189416C")
        )
      )
    ),
    None,
    Declarations(true, true, true, Some(true), None, None, Some(true))
  )

  val finalSubmission1 = FinalSubmission(
    CalculationInputs(
      Resubmission(false, None),
      Setup(
        Some(
          AnnualAllowanceSetup(
            Some(true),
            Some(false),
            Some(false),
            Some(false),
            Some(false),
            Some(false),
            Some(MaybePIAIncrease.No),
            Some(MaybePIAUnchangedOrDecreased.No),
            Some(false),
            Some(false),
            Some(false),
            Some(false)
          )
        ),
        Some(
          LifetimeAllowanceSetup(
            Some(true),
            Some(false),
            Some(true),
            Some(false),
            Some(false),
            Some(false),
            Some(true)
          )
        )
      ),
      Some(
        AnnualAllowance(
          List(Period._2021, Period._2019),
          List(
            TaxYear2011To2015(20000, Period._2011),
            TaxYear2011To2015(50000, Period._2013),
            TaxYear2011To2015(50000, Period._2014),
            TaxYear2011To2015(40000, Period._2015),
            PostFlexiblyAccessedTaxYear(
              0,
              20000,
              60000,
              0,
              List(TaxYearScheme("Scheme 1", "00348916RT", 20000, 0, Some(36000))),
              Period._2016,
              incomeSubJourney,
              None
            ),
            NormalTaxYear(
              45000,
              List(TaxYearScheme("Scheme 1", "00348916RT", 45000, 0, None)),
              60000,
              0,
              Period._2017,
              incomeSubJourney,
              Some(BelowThreshold)
            ),
            NormalTaxYear(
              38000,
              List(TaxYearScheme("Scheme 1", "00348916RT", 38000, 200, None)),
              60000,
              200,
              Period._2018,
              incomeSubJourney,
              Some(BelowThreshold)
            ),
            NormalTaxYear(
              43000,
              List(TaxYearScheme("Scheme 1", "00348916RT", 43000, 0, None)),
              60000,
              3280,
              Period._2019,
              incomeSubJourney,
              Some(BelowThreshold)
            ),
            NormalTaxYear(
              45000,
              List(TaxYearScheme("Scheme 1", "00348916RT", 45000, 9000, None)),
              180000,
              0,
              Period._2020,
              incomeSubJourney,
              Some(AboveThreshold(160000))
            ),
            NormalTaxYear(
              42000,
              List(TaxYearScheme("Scheme 1", "00348916RT", 42000, 0, None)),
              150000,
              0,
              Period._2021,
              incomeSubJourney,
              Some(BelowThreshold)
            ),
            NormalTaxYear(
              43500,
              List(TaxYearScheme("Scheme 1", "00348916RT", 43500, 2400, None)),
              60000,
              0,
              Period._2022,
              incomeSubJourney,
              Some(BelowThreshold)
            ),
            NormalTaxYear(
              42000,
              List(TaxYearScheme("Scheme 1", "00348916RT", 42000, 0, None)),
              60000,
              0,
              Period._2023,
              incomeSubJourney,
              Some(BelowThreshold)
            )
          )
        )
      ),
      Some(
        LifeTimeAllowance(
          LocalDate.parse("2018-10-27"),
          LtaProtectionOrEnhancements.Protection,
          Some(ProtectionType.FixedProtection2014),
          Some("R41AB678TR23355"),
          ProtectionEnhancedChanged.Protection,
          Some(WhatNewProtectionTypeEnhancement.IndividualProtection2014),
          Some("2134567801"),
          true,
          Some(ExcessLifetimeAllowancePaid.Annualpayment),
          Some(WhoPaidLTACharge.PensionScheme),
          Some(SchemeNameAndTaxRef("Scheme 1", "00348916RT")),
          Some(WhoPayingExtraLtaCharge.You),
          None,
          NewLifeTimeAllowanceAdditions(
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None
          )
        )
      )
    ),
    Some(
      CalculationResponse(
        responseResubmission(false, None),
        TotalAmounts(10470, 1620, 5500),
        List(
          OutOfDatesTaxYearsCalculation(
            responsePeriod._2016,
            0,
            0,
            0,
            0,
            0,
            0,
            40000,
            List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0)),
            Some(0)
          ),
          OutOfDatesTaxYearsCalculation(
            responsePeriod._2017,
            0,
            0,
            0,
            0,
            5000,
            2000,
            0,
            List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0)),
            Some(0)
          ),
          OutOfDatesTaxYearsCalculation(
            responsePeriod._2018,
            200,
            200,
            200,
            200,
            0,
            0,
            2000,
            List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 200)),
            Some(0)
          ),
          OutOfDatesTaxYearsCalculation(
            responsePeriod._2019,
            2870,
            0,
            3280,
            0,
            1000,
            410,
            0,
            List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0)),
            Some(0)
          )
        ),
        List(
          InDatesTaxYearsCalculation(
            responsePeriod._2020,
            0,
            4500,
            0,
            0,
            9000,
            10000,
            4500,
            0,
            List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 9000)),
            Some(0)
          ),
          InDatesTaxYearsCalculation(
            responsePeriod._2021,
            0,
            0,
            820,
            0,
            0,
            2000,
            820,
            0,
            List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0)),
            Some(0)
          ),
          InDatesTaxYearsCalculation(
            responsePeriod._2022,
            0,
            1000,
            300,
            0,
            2400,
            3500,
            1400,
            0,
            List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 2400)),
            Some(0)
          ),
          InDatesTaxYearsCalculation(
            responsePeriod._2023,
            0,
            0,
            800,
            0,
            0,
            2000,
            800,
            0,
            List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0)),
            Some(0)
          )
        )
      )
    ),
    submissionInputs1
  )

  val finalSubmission2 = finalSubmission1.copy(submissionInputs = submissionInputs2)

}
