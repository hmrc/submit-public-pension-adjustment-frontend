package pages

import models.UserAnswers
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object HowMuchTaxReliefPage extends QuestionPage[Int] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "howMuchTaxRelief"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    answers.get(HowMuchTaxReliefPage) match {
      case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad
      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = {
    answers.get(HowMuchTaxReliefPage) match {
      case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad
      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }
}
