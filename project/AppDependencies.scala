
import play.core.PlayVersion
import play.sbt.PlayImport.*
import sbt.Keys.libraryDependencies
import sbt.*

object AppDependencies {

  private val bootstrapVersion = "7.22.0"

  def apply(): Seq[ModuleID] = compileDeps ++ testDeps

  val compileDeps = Seq(
    "uk.gov.hmrc"    %% "bootstrap-backend-play-28"          % bootstrapVersion,
    "uk.gov.hmrc"    %% "api-platform-common-domain"         % "0.5.0"
  )

  val testDeps = Seq(
    "uk.gov.hmrc"    %% "bootstrap-test-play-28"     % bootstrapVersion,
    "org.mockito"    %% "mockito-scala-scalatest"    % "1.17.22"
  ).map(_ % "test, it")
}
