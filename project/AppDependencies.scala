
import play.core.PlayVersion
import play.sbt.PlayImport.*
import sbt.Keys.libraryDependencies
import sbt.*

object AppDependencies {

  private val bootstrapVersion = "8.4.0"
  val apiDomainVersion = "0.11.0"
  val commonDomainVersion = "0.10.0"
  val appDomainVersion = "0.32.0"

  def apply(): Seq[ModuleID] = compileDeps ++ testDeps

  val compileDeps = Seq(
    "uk.gov.hmrc"    %% "bootstrap-backend-play-30"         % bootstrapVersion,
    "uk.gov.hmrc"    %% "api-platform-application-domain"   % appDomainVersion,
    "uk.gov.hmrc"    %% "api-platform-api-domain"           % apiDomainVersion,
    "uk.gov.hmrc"    %% "internal-auth-client-play-30"      % "1.10.0"
  )

  val testDeps = Seq(
    "uk.gov.hmrc"    %% "bootstrap-test-play-30"            % bootstrapVersion,
    "org.mockito"    %% "mockito-scala-scalatest"           % "1.17.29",
    "uk.gov.hmrc"    %% "api-platform-test-common-domain"   % commonDomainVersion
  ).map(_ % "test, it")
}
