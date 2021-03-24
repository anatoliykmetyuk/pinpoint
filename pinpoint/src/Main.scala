package pinpoint

import java.io.File
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

import org.apache.commons.io.FileUtils
import org.json.{ JSONObject, JSONArray }


sealed trait Marker
case class Single(hash: Int) extends Marker
case class Range(start: Int, end: Int) extends Marker

case class Settings(markers: List[Marker])
def readSettingsFromProjectFile(filename: String = "pinpoint-cfg.json") =
  val jsonString = FileUtils.readFileToString(File(filename), "utf8")
  val data = JSONObject(jsonString)
  Settings(
    markers = data.getJSONArray("markers").asScala.toList.map {
      case id: Number => Single(id.intValue)
      case ab: JSONArray => ab.asScala.toList match
        case (start: Number) :: (end: Number) :: Nil => Range(start.intValue, end.intValue)
    }.toList
  )

type Timeline = ListBuffer[String]

private val timelines = ListBuffer.empty[ListBuffer[String]]
private def reset() = timelines.clear()

def log(msg: String, level: Int = 0, readSettings: => Settings = readSettingsFromProjectFile()): Unit =
  val settings = readSettings

  while timelines.length < settings.markers.length + 1
  do timelines.append(ListBuffer.empty[String])

  val allowedLogAtLevel: Boolean =
    settings.markers.take(level).zip(timelines.take(level))  // All the prior timelines and their marked hashes
      .forall {
        case (Single(id), tmln) => tmln.length == id
        case (Range(start, end), tmln) => tmln.length >= start && tmln.length <= end
      }

  if level < timelines.length && allowedLogAtLevel then  // Ignore levels beyond the innermost timeline
    timelines(level).append(msg)
    if level == timelines.length - 1  // Print the innermost timeline only
    then
      val currentId = "%03d".format(timelines(level).length)
      println(s"""\u001b[43;1m\u001b[30m${currentId}\u001b[0m $msg""")
