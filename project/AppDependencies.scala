import sbt.*

object AppDependencies {

  private val bootstrapVersion    = "10.8.0"
  private val commonDomainVersion = "1.4.0"
  private val appDomainVersion    = "1.6.0"
  private val apiDomainVersion    = "1.8.0"
  private val tpdDomainVersion    = "1.3.0"
  private val mockitoScalaVersion = "2.2.1"
  
  def apply(): Seq[ModuleID] = compileDeps ++ testDeps

  val compileDeps = Seq(
    "uk.gov.hmrc"    %% "bootstrap-backend-play-30"         % bootstrapVersion,
    "uk.gov.hmrc"    %% "api-platform-common-domain"        % commonDomainVersion,
    "uk.gov.hmrc"    %% "api-platform-application-domain"   % appDomainVersion,
    "uk.gov.hmrc"    %% "api-platform-api-domain"           % apiDomainVersion,
    "uk.gov.hmrc"    %% "api-platform-tpd-domain"           % tpdDomainVersion,
    "uk.gov.hmrc"    %% "internal-auth-client-play-30"      % "4.4.0"
  )

  val testDeps = Seq(
    "uk.gov.hmrc"    %% "bootstrap-test-play-30"                    % bootstrapVersion,
    "org.mockito"    %% "mockito-scala-scalatest"                   % mockitoScalaVersion,
    "uk.gov.hmrc"    %% "api-platform-common-domain-fixtures"       % commonDomainVersion,
    "uk.gov.hmrc"    %% "api-platform-application-domain-fixtures"  % appDomainVersion,
    "uk.gov.hmrc"    %% "api-platform-tpd-domain-fixtures"          % tpdDomainVersion
  ).map(_ % "test")
}
