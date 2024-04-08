package pages

import models.NormalMode

class ConfirmRestartAnswersPageSpec extends PageBehaviours {

  "ConfirmRestartAnswersPage" - {

    beRetrievable[Boolean](ConfirmRestartAnswersPage)

    beSettable[Boolean](ConfirmRestartAnswersPage)

    beRemovable[Boolean](ConfirmRestartAnswersPage)
  }

  "to restart calculation controller when yes" in {
    val ua = emptyUserAnswers
      .set(ConfirmRestartAnswersPage, true)
      .get

    val nextPageUrl: String = ConfirmRestartAnswersPage.navigate(NormalMode, ua).url

    checkNavigation(nextPageUrl, "/restart-calculation")
  }

  "to continue choice page when no" in {

    val ua = emptyUserAnswers
      .set(ConfirmRestartAnswersPage, false)
      .get

    val nextPageUrl: String = ConfirmRestartAnswersPage.navigate(NormalMode, ua).url

    checkNavigation(nextPageUrl, "/continue-choice")

  }

  "to journey recover when not answered" in {

    val nextPageUrl: String = ConfirmRestartAnswersPage.navigate(NormalMode, emptyUserAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }
}
