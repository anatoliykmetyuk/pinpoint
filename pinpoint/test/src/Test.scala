package pinpoint

import utest._

object PinpointTest extends TestSuite:
  val tests = Tests {
    test("Multilivel logging") {
      val s = Settings(
        markers = List(Single(4), Single(11)),
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

    test("Range logging") {
      reset()
      val s = Settings(
        markers = List(Single(4), Range(4, 6)),
      )

      for i <- 1 to 10 do
        log(s"Got $i", 0, s)
        val squared = i * i
        log(s"Squared: $squared", 1, s)
        for x <- 1 to squared do
          val cubed = x * x * x
          log(s"Cubed: $cubed", 1, s)
          log(s"I'm 27, 64, 125", 2, s)
    }

    test("Reading config from file") {
      assert(
        readSettingsFromProjectFile("pinpoint/test/resources/hash.json") == Settings(
          markers = List(
            Single(1),
            Single(2),
            Range(3, 4)
          ),
        )
      )
    }
  }
