import sbt.*

object AppDependencies {
  import play.core.PlayVersion

  private val bootstrapPlay30Version = "9.4.0"
  private val mongoPlay30Version     = "1.9.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,

    "uk.gov.hmrc"                  %% "play-frontend-hmrc-play-30"    % "8.5.0",
    "uk.gov.hmrc"                  %% "play-conditional-form-mapping-play-30" % "2.0.0",
    "uk.gov.hmrc"                  %% "domain-play-30"                        % "9.0.0",
    "uk.gov.hmrc"                  %% "bootstrap-frontend-play-30"    % bootstrapPlay30Version,
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-30"            % mongoPlay30Version,
    "com.googlecode.libphonenumber" % "libphonenumber"                % "8.13.34",
    "com.beachape"                 %% "enumeratum-play"               % "1.8.0",
    "uk.gov.hmrc"                  %% "crypto-json-play-30"           % "8.0.0"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"  % bootstrapPlay30Version % Test,
    "org.scalatest"          %% "scalatest"               % "3.2.10" % Test,
    "org.scalatestplus"      %% "scalacheck-1-15"         % "3.2.10.0" % Test,
    "org.scalatestplus"      %% "mockito-3-4"             % "3.2.10.0" % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"      % "7.0.1" % Test,
    "org.pegdown"             % "pegdown"                 % "1.6.0" % Test,
    "org.jsoup"               % "jsoup"                   % "1.14.3" % Test,
    "org.playframework"      %% "play-test"               % PlayVersion.current % Test,
    "org.mockito"            %% "mockito-scala"           % "1.17.29" % Test,
    "org.scalacheck"         %% "scalacheck"              % "1.15.4" % Test,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-30" % mongoPlay30Version % Test,
    "com.vladsch.flexmark"    % "flexmark-all"            % "0.62.2" % Test
  )

  val itDependencies = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapPlay30Version % Test
  )
  def apply(): Seq[ModuleID] = compile ++ test
}
