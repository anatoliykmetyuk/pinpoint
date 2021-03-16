package pinpoint

import utest._

object PinpointTest extends TestSuite:
  val tests = Tests {
    test("Multilivel logging") {
      val s = Settings(
        markers = List("2eff675c", "5b032a94"),
        hashSize = 8,
      )

      for i <- 1 to 10 do
        log(s"Got $i", 0, s)
        val squared = i * i
        log(s"Squared: $squared", 1, s)
        for x <- 1 to squared do
          val cubed = x * x * x
          log(s"Cubed: $cubed", 1, s)
          log(s"I am 1000! $cubed", 2, s)
    }

    test("Reading config from file") {
      assert(
        readSettingsFromProjectFile("pinpoint/test/resources/hash.json") == Settings(
          markers = List("133faaca", "55e2e195"),
          hashSize = 8
        )
      )
    }
  }
