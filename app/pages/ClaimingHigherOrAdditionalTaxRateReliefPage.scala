package pages

import models.UserAnswers
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object ClaimingHigherOrAdditionalTaxRateReliefPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "claimingHigherOrAdditionalTaxRateRelief"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    answers.get(ClaimingHigherOrAdditionalTaxRateReliefPage) match {
      case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad
      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = {
    answers.get(ClaimingHigherOrAdditionalTaxRateReliefPage) match {
      case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad
      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }
}
