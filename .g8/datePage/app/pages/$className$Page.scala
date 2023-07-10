package pages

import java.time.LocalDate
import models.UserAnswers
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object $className$Page extends QuestionPage[LocalDate] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "$className;format="decap"$"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    answers.get($className$Page) match {
      case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad()
      case _ => routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = {
    answers.get($className$Page) match {
      case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad()
      case _ => routes.JourneyRecoveryController.onPageLoad(None)
    }
  }
}
