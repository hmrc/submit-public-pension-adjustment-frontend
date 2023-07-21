package pages

import java.time.LocalDate
import models.UserAnswers
import play.api.libs.json.JsPath
import play.api.mvc.Call
import controllers.routes

case object MemberDateOfDeathPage extends QuestionPage[LocalDate] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "memberDateOfDeath"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    answers.get(MemberDateOfDeathPage) match {
      case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad
      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call = {
    answers.get(MemberDateOfDeathPage) match {
      case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad
      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }
}
