package pages


class PensionSchemeMemberNamePageSpec extends PageBehaviours {

  "PensionSchemeMemberNamePage" - {

    beRetrievable[String](PensionSchemeMemberNamePage)

    beSettable[String](PensionSchemeMemberNamePage)

    beRemovable[String](PensionSchemeMemberNamePage)
  }
}
