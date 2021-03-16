package pinpoint

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter
import scala.collection.mutable.ListBuffer
import scala.util.{ Try, Success, Failure }

import ujson._


case class Settings(markers: List[String], hashSize: Int)
def readSettingsFromProjectFile(filename: String = "pinpoint-cfg.json") =
  val jsonString = os.read(os.RelPath(filename).resolveFrom(os.pwd))
  val data = ujson.read(jsonString)
  Settings(
    markers = data("markers").arr.map(_.str).toList,
    hashSize = data("hash_size").num.toInt
  )

type Timeline = ListBuffer[String]

private val timelines = ListBuffer.empty[ListBuffer[String]]

private def hash(timeline: ListBuffer[String]): String =
  val md = MessageDigest.getInstance("MD5")
  md.update(timeline.mkString.getBytes)
  val digest: Array[Byte] = md.digest()
  DatatypeConverter.printHexBinary(digest).toLowerCase

def log(msg: String, level: Int = 0, readSettings: => Settings = readSettingsFromProjectFile()): Unit =
  val settings = readSettings

  while timelines.length < settings.markers.length + 1
  do timelines.append(ListBuffer.empty[String])

  val allowedLogAtLevel: Boolean =
    settings.markers.take(level).zip(timelines.take(level))  // All the prior timelines and their marked hashes
      .forall { case (h, tmln) => hash(tmln).startsWith(h) }          // All the prior timelines are at mark

  if level < timelines.length && allowedLogAtLevel then  // Ignore levels beyond the innermost timeline
    timelines(level).append(msg)
    if level == timelines.length - 1  // Print the innermost timeline only
    then
      val currentHash = hash(timelines(level)).take(settings.hashSize)
      println(s"""\u001b[43;1m\u001b[30m${currentHash}\u001b[0m $msg""")
