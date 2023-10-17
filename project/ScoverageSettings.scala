import scoverage.ScoverageKeys

object ScoverageSettings {
  def apply() = Seq(
    ScoverageKeys.coverageExcludedPackages := Seq(
      "<empty>",
      """.*\.controllers\.binders""",
      """uk\.gov\.hmrc\.BuildInfo""" ,
      """.*\.Routes""" ,
      """.*\.RoutesPrefix""" ,
      """.*\.Reverse[^.]*"""
    ).mkString(";"),
    ScoverageKeys.coverageMinimumStmtTotal := 95.00,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}
