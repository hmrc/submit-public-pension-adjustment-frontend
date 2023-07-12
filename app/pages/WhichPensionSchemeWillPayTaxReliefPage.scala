package pages

import models.WhichPensionSchemeWillPayTaxRelief
import models.UserAnswers
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object WhichPensionSchemeWillPayTaxReliefPage extends QuestionPage[WhichPensionSchemeWillPayTaxRelief] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "whichPensionSchemeWillPayTaxRelief"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(WhichPensionSchemeWillPayTaxReliefPage) match {
      case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(WhichPensionSchemeWillPayTaxReliefPage) match {
      case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
}
