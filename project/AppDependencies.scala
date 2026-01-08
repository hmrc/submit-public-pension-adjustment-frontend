import sbt.*

object AppDependencies {
  import play.core.PlayVersion

  private val bootstrapPlay30Version = "10.5.0"
  private val mongoPlay30Version     = "2.11.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,

    "uk.gov.hmrc"                   %% "play-frontend-hmrc-play-30"            % "12.25.0",
    "uk.gov.hmrc"                   %% "play-conditional-form-mapping-play-30" % "3.4.0",
    "uk.gov.hmrc"                   %% "domain-play-30"                        % "13.0.0",
    "uk.gov.hmrc"                   %% "bootstrap-frontend-play-30"     % bootstrapPlay30Version,
    "uk.gov.hmrc.mongo"             %% "hmrc-mongo-play-30"             % mongoPlay30Version,
    "com.googlecode.libphonenumber" % "libphonenumber"                  % "9.0.21",
    "com.beachape"                  %% "enumeratum-play"                % "1.9.2",
    "uk.gov.hmrc"                   %% "crypto-json-play-30"            % "8.4.0"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"  % bootstrapPlay30Version % Test,
    "org.scalatest"          %% "scalatest"               % "3.2.19" % Test,
    "org.scalacheck"         %% "scalacheck"              % "1.19.0" % Test,
    "org.scalatestplus"      %% "scalacheck-1-17"         % "3.2.18.0" % Test,
    "org.scalatestplus"      %% "mockito-4-11"            % "3.2.18.0" % Test,
    "org.jsoup"               % "jsoup"                   % "1.22.1" % Test,
    "org.playframework"      %% "play-test"               % PlayVersion.current % Test,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-30" % mongoPlay30Version % Test,
    "com.vladsch.flexmark"    % "flexmark-all"            % "0.64.8" % Test
  )

  val itDependencies = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapPlay30Version % Test
  )
  def apply(): Seq[ModuleID] = compile ++ test
}
