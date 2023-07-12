package controllers

import controllers.actions._
import forms.WhichPensionSchemeWillPayTaxReliefFormProvider
import javax.inject.Inject
import models.Mode
import pages.WhichPensionSchemeWillPayTaxReliefPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.WhichPensionSchemeWillPayTaxReliefView

import scala.concurrent.{ExecutionContext, Future}

class WhichPensionSchemeWillPayTaxReliefController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: WhichPensionSchemeWillPayTaxReliefFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: WhichPensionSchemeWillPayTaxReliefView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) { implicit request =>
    val preparedForm = request.userAnswers.get(WhichPensionSchemeWillPayTaxReliefPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(WhichPensionSchemeWillPayTaxReliefPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(WhichPensionSchemeWillPayTaxReliefPage.navigate(mode, updatedAnswers))
        )
  }
}
