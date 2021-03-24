import mill._, scalalib._, publish._

object pinpoint extends ScalaModule with PublishModule {
  def scalaVersion = "3.0.0-RC2-bin-20210318-e60ef35-NIGHTLY"
  def publishVersion = "0.1.0"

  def pomSettings = PomSettings(
    description = "Dotty issues pinpointer",
    organization = "com.akmetiuk",
    url = "https://github.com/anatoliykmetyuk/pinpoint",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("anatoliykmetyuk", "pinpoint"),
    developers = Seq(
      Developer("anatoliykmetyuk", "Anatolii Kmetiuk", "https://github.com/anatoliykmetyuk")
    )
  )

  def ivyDeps = Agg(
    ivy"org.json:json:20210307",
    ivy"commons-io:commons-io:2.8.0",
  )

  override def docJar = T {
    val outDir = T.ctx().dest
    val javadocDir = outDir / 'javadoc
    os.makeDir.all(javadocDir)
    mill.api.Result.Success(mill.modules.Jvm.createJar(Agg(javadocDir))(outDir))
  }

  object test extends ScalaModule with TestModule with Tests {
    def testFrameworks = Seq("com.novocode.junit.JUnitFramework")
    def ivyDeps = Agg(ivy"com.novocode:junit-interface:0.11")
  }
}
