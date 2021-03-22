package pinpoint

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter
import scala.collection.mutable.ListBuffer
import scala.util.{ Try, Success, Failure }

import ujson._

sealed trait Marker
case class Single(hash: Int) extends Marker
case class Range(start: Int, end: Int) extends Marker

case class Settings(markers: List[Marker])
def readSettingsFromProjectFile(filename: String = "pinpoint-cfg.json") =
  val jsonString = os.read(os.RelPath(filename).resolveFrom(os.pwd))
  val data = ujson.read(jsonString)
  Settings(
    markers = data("markers").arr.map {
      case Num(id) => Single(id.toInt)
      case Arr(ab) => ab.toList match
        case Num(start) :: Num(end) :: Nil => Range(start.toInt, end.toInt)
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
