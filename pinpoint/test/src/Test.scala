package pinpoint

import utest._

object PinpointTest extends TestSuite:
  val tests = Tests {
    test("Hello") {
      def dummySettings() = Settings(
        inspectAtHash = Some("3858f") // Hash of trace("bar")
      )

      trace("foo", dummySettings())
      inspect("AAA", dummySettings())
      trace("bar", dummySettings())
      inspect("BBB", dummySettings())
      trace("jar", dummySettings())
    }
  }
