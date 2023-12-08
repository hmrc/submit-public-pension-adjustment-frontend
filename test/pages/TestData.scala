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

package pages

import models.{PSTR, PensionSchemeMemberInternationalAddress, UkAddress, UserAnswers}
import models.calculation.inputs.Income.{AboveThreshold, BelowThreshold}
import models.calculation.inputs.TaxYear2016To2023.{NormalTaxYear, PostFlexiblyAccessedTaxYear}
import models.calculation.inputs._
import models.calculation.response.{CalculationResponse, InDatesTaxYearSchemeCalculation, InDatesTaxYearsCalculation, OutOfDatesTaxYearSchemeCalculation, OutOfDatesTaxYearsCalculation, Period => responsePeriod, Resubmission => responseResubmission, TaxYearScheme, TotalAmounts}
import models.finalsubmission.OnBehalfOfMemberType.PowerOfAttorney
import models.finalsubmission.{AdministrativeDetails, AuthRetrievals, ClaimantDetails, Declarations, FinalSubmission, IndividualSchemeIdentifier, OnBehalfOfMember, PaymentElection, PersonalCharge, PersonalDetails, SchemeCharge, SchemeDetails, SchemeTaxRelief, SubmissionInputs, TaxIdentifiers}
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
    benefitCrystallisationEventFlag = true,
    benefitCrystallisationEventDate = LocalDate.of(2017, 1, 30),
    changeInLifetimeAllowancePercentageInformedFlag = true,
    changeInTaxCharge = ChangeInTaxCharge.NewCharge,
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
      false,
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
      "Test UserId",
      "AA000000A",
      "Test User",
      Some("1234567890"),
      Some(LocalDate.of(1968, 11, 30))
    )

  val calculationInputs = CalculationInputs(
    Resubmission(false, None),
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
            List(TaxYearScheme("Scheme 1", "00348916RT", 30000, 20000, 0)),
            Period._2016PreAlignment,
            None
          ),
          PostFlexiblyAccessedTaxYear(
            0,
            18000,
            60000,
            3600,
            List(TaxYearScheme("Scheme 1", "00348916RT", 30000, 22000, 3600)),
            Period._2016PostAlignment,
            None
          ),
          NormalTaxYear(
            45000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 38000, 45000, 0)),
            60000,
            0,
            Period._2017,
            Some(BelowThreshold)
          ),
          NormalTaxYear(
            38000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 43000, 38000, 200)),
            60000,
            200,
            Period._2018,
            Some(BelowThreshold)
          ),
          NormalTaxYear(
            43000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 48000, 43000, 0)),
            60000,
            3280,
            Period._2019,
            Some(BelowThreshold)
          ),
          NormalTaxYear(
            45000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 50000, 45000, 9000)),
            180000,
            0,
            Period._2020,
            Some(AboveThreshold(160000))
          ),
          NormalTaxYear(
            42000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 38000, 42000, 0)),
            150000,
            0,
            Period._2021,
            Some(BelowThreshold)
          ),
          NormalTaxYear(
            43500,
            List(TaxYearScheme("Scheme 1", "00348916RT", 48000, 43500, 2400)),
            60000,
            0,
            Period._2022,
            Some(BelowThreshold)
          ),
          NormalTaxYear(
            42000,
            List(TaxYearScheme("Scheme 1", "00348916RT", 38000, 42000, 0)),
            60000,
            0,
            Period._2023,
            Some(BelowThreshold)
          )
        )
      )
    ),
    Some(
      LifeTimeAllowance(
        true,
        LocalDate.parse("2018-10-27"),
        true,
        ChangeInTaxCharge.IncreasedCharge,
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
          false,
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
          responsePeriod._2016PreAlignment,
          0,
          0,
          0,
          0,
          0,
          0,
          40000,
          List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0))
        ),
        OutOfDatesTaxYearsCalculation(
          responsePeriod._2016PostAlignment,
          3600,
          3600,
          3600,
          3600,
          0,
          0,
          0,
          List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 3600))
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
          List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0))
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
          List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 200))
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
          List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0))
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
          List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 9000))
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
          List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0))
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
          List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 2400))
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
          List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0))
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
      |  "whichPensionSchemeWillPayTaxRelief": "Scheme 1 / 00348916RT"
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

  val userAnswers3 = UserAnswers(
    "Test UserId",
    userAnswerData3
  )

  val submissionInputs1 = SubmissionInputs(
    AdministrativeDetails(
      ClaimantDetails(
        PersonalDetails(
          "Test User",
          None,
          Some(LocalDate.of(1968, 11, 30)),
          Some(UkAddress("Test Address line 1", None, "Test city", Some("Test county"), "AB1 2CD")),
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
          SchemeCharge(820, SchemeDetails("Scheme 1", PSTR("00348916RT")), Some(LocalDate.parse("2021-07-20")), None)
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
            None,
            Some("1 October 2024 to 31 December 2024")
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
    Declarations(true, true, true, None, None)
  )

  val submissionInputs2 = SubmissionInputs(
    AdministrativeDetails(
      ClaimantDetails(
        PersonalDetails(
          "Test User",
          Some("Duplicate Name"),
          Some(LocalDate.of(1968, 11, 30)),
          Some(UkAddress("Test UK Address line 1", None, "Test UK city", None, "AB1 2CD")),
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
              PensionSchemeMemberInternationalAddress(
                "Test Address line 1",
                Some("Test Address line 2"),
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
    Declarations(true, true, true, Some(true), None)
  )

  val finalSubmission1 = FinalSubmission(
    CalculationInputs(
      Resubmission(false, None),
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
              List(TaxYearScheme("Scheme 1", "00348916RT", 30000, 20000, 0)),
              Period._2016PreAlignment,
              None
            ),
            PostFlexiblyAccessedTaxYear(
              0,
              18000,
              60000,
              3600,
              List(TaxYearScheme("Scheme 1", "00348916RT", 30000, 22000, 3600)),
              Period._2016PostAlignment,
              None
            ),
            NormalTaxYear(
              45000,
              List(TaxYearScheme("Scheme 1", "00348916RT", 38000, 45000, 0)),
              60000,
              0,
              Period._2017,
              Some(BelowThreshold)
            ),
            NormalTaxYear(
              38000,
              List(TaxYearScheme("Scheme 1", "00348916RT", 43000, 38000, 200)),
              60000,
              200,
              Period._2018,
              Some(BelowThreshold)
            ),
            NormalTaxYear(
              43000,
              List(TaxYearScheme("Scheme 1", "00348916RT", 48000, 43000, 0)),
              60000,
              3280,
              Period._2019,
              Some(BelowThreshold)
            ),
            NormalTaxYear(
              45000,
              List(TaxYearScheme("Scheme 1", "00348916RT", 50000, 45000, 9000)),
              180000,
              0,
              Period._2020,
              Some(AboveThreshold(160000))
            ),
            NormalTaxYear(
              42000,
              List(TaxYearScheme("Scheme 1", "00348916RT", 38000, 42000, 0)),
              150000,
              0,
              Period._2021,
              Some(BelowThreshold)
            ),
            NormalTaxYear(
              43500,
              List(TaxYearScheme("Scheme 1", "00348916RT", 48000, 43500, 2400)),
              60000,
              0,
              Period._2022,
              Some(BelowThreshold)
            ),
            NormalTaxYear(
              42000,
              List(TaxYearScheme("Scheme 1", "00348916RT", 38000, 42000, 0)),
              60000,
              0,
              Period._2023,
              Some(BelowThreshold)
            )
          )
        )
      ),
      Some(
        LifeTimeAllowance(
          true,
          LocalDate.parse("2018-10-27"),
          true,
          ChangeInTaxCharge.IncreasedCharge,
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
            false,
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
            responsePeriod._2016PreAlignment,
            0,
            0,
            0,
            0,
            0,
            0,
            40000,
            List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0))
          ),
          OutOfDatesTaxYearsCalculation(
            responsePeriod._2016PostAlignment,
            3600,
            3600,
            3600,
            3600,
            0,
            0,
            0,
            List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 3600))
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
            List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0))
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
            List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 200))
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
            List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0))
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
            List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 9000))
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
            List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0))
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
            List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 2400))
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
            List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0))
          )
        )
      )
    ),
    submissionInputs1
  )

  val finalSubmission2 = finalSubmission1.copy(submissionInputs = submissionInputs2)

}
