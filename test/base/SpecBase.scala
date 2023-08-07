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

package base

import controllers.actions._
import models.UserAnswers
import models.calculation.response.{CalculationResponse, InDatesTaxYearsCalculation, Period, TotalAmounts}
import models.calculation.inputs.{CalculationInputs, Resubmission}
import models.submission.Submission
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest

import java.time.{Clock, Instant, LocalDate, ZoneId}

trait SpecBase
    extends AnyFreeSpec
    with Matchers
    with TryValues
    with OptionValues
    with ScalaFutures
    with IntegrationPatience {

  val userAnswersId: String = "id"

  def aCalculationResponseWithAnInDateDebitYear = {

    val inDatesYears = List(
      inDatesTaxYearsCalculation(models.calculation.response.Period._2023, 0),
      inDatesTaxYearsCalculation(models.calculation.response.Period._2022, 1),
      inDatesTaxYearsCalculation(models.calculation.response.Period._2020, 1),
      inDatesTaxYearsCalculation(models.calculation.response.Period._2021, 1)
    )

    val calculationResponse = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(0, 1, 0),
      List.empty,
      inDatesYears
    )
    calculationResponse
  }

  private def inDatesTaxYearsCalculation(period1: Period, debitAmount: Int) =
    InDatesTaxYearsCalculation(
      period = period1,
      memberCredit = 0,
      schemeCredit = 0,
      debit = debitAmount,
      chargePaidByMember = 0,
      chargePaidBySchemes = 0,
      revisedChargableAmountBeforeTaxRate = 0,
      revisedChargableAmountAfterTaxRate = 0,
      unusedAnnualAllowance = 0,
      taxYearSchemes = List.empty
    )

  val sessionId: String = "sessionId"

  val uniqueId: String = "uniqueId"

  def emptyUserAnswers: UserAnswers = UserAnswers(userAnswersId)

  val resubmission = Resubmission(false, None)

  val calculationInputs = CalculationInputs(resubmission, None, None)

  val submission = Submission(sessionId, uniqueId, calculationInputs, None)

  protected val fixedInstant: Instant      = LocalDate.now.atStartOfDay(ZoneId.systemDefault).toInstant
  protected val clockAtFixedInstant: Clock = Clock.fixed(fixedInstant, ZoneId.systemDefault)

  def messages(app: Application): Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  protected def applicationBuilder(
    userAnswers: Option[UserAnswers] = None,
    submission: Option[Submission] = None
  ): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[CalculationDataRequiredAction].to[CalculationDataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[LandingPageIdentifierAction].to[FakeLandingPageIdentifierAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers, submission))
      )
}
