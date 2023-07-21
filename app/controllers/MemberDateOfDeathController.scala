package controllers

import controllers.actions._
import forms.MemberDateOfDeathFormProvider
import javax.inject.Inject
import models.Mode
import pages.MemberDateOfDeathPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.MemberDateOfDeathView

import scala.concurrent.{ExecutionContext, Future}

class MemberDateOfDeathController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: MemberDateOfDeathFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: MemberDateOfDeathView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
    implicit request =>

      val preparedForm = request.userAnswers.get(MemberDateOfDeathPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(MemberDateOfDeathPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(MemberDateOfDeathPage.navigate(mode, updatedAnswers))
      )
  }
}
