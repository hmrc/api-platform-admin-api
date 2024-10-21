import sbt._

object AppDependencies {

  private val bootstrapVersion = "9.0.0"
  private val appDomainVersion = "0.61.12"
  private val apiDomainVersion = "0.19.1"
  private val tpdDomainVersion = "0.10.0"
  
  def apply(): Seq[ModuleID] = compileDeps ++ testDeps

  val compileDeps = Seq(
    "uk.gov.hmrc"    %% "bootstrap-backend-play-30"         % bootstrapVersion,
    "uk.gov.hmrc"    %% "api-platform-application-domain"   % appDomainVersion,
    "uk.gov.hmrc"    %% "api-platform-api-domain"           % apiDomainVersion,
    "uk.gov.hmrc"    %% "api-platform-tpd-domain"           % tpdDomainVersion,
    "uk.gov.hmrc"    %% "internal-auth-client-play-30"      % "3.0.0"
  )

  val testDeps = Seq(
    "uk.gov.hmrc"    %% "bootstrap-test-play-30"                    % bootstrapVersion,
    "org.mockito"    %% "mockito-scala-scalatest"                   % "1.17.30",
    "uk.gov.hmrc"    %% "api-platform-application-domain-fixtures"  % appDomainVersion,
    "uk.gov.hmrc"    %% "api-platform-test-tpd-domain"              % tpdDomainVersion
  ).map(_ % "test")
}
