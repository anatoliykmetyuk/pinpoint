package pinpoint

import utest._

object PinpointTest extends TestSuite:
  val tests = Tests {
    test("Tracing and inspecting") {
      def dummySettings() = Settings(
        inspectAtHash = Some("3858f") // Hash of trace("bar")
      )

      trace("foo", dummySettings())
      inspect("AAA", dummySettings())
      trace("bar", dummySettings())
      inspect("BBB", dummySettings())
      trace("jar", dummySettings())
    }

    test("Reading config from file") {
      assert(
        readSettingsFromProjectFile("pinpoint/test/resources/hash.json") == Settings(
          inspectAtHash = Some("3d29a")
        )
      )

      assert(
        readSettingsFromProjectFile("pinpoint/test/resources/empty.json") == Settings(
          inspectAtHash = None
        )
      )
    }
  }
