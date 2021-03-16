package pinpoint

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter
import scala.collection.mutable.ListBuffer
import scala.util.{ Try, Success, Failure }

import ujson._

case class Settings(inspectAtHash: Option[String])
def readSettingsFromProjectFile(filename: String = "pinpoint-cfg.json") =
  val jsonString = os.read(os.RelPath(filename).resolveFrom(os.pwd))
  val data = ujson.read(jsonString)
  Try(data("inspect_at_hash")).map(_.str) match
    case Success(value) =>
      Settings(
        inspectAtHash = Some(value)
      )
    case Failure(_) =>
      Settings(
        inspectAtHash = None
      )


private val log = ListBuffer.empty[String]

private def currentHash: String =
  val md = MessageDigest.getInstance("MD5")
  md.update(log.mkString.getBytes)
  val digest: Array[Byte] = md.digest()
  DatatypeConverter.printHexBinary(digest).toLowerCase

def trace(msg: String, settings: => Settings = readSettingsFromProjectFile()): Unit =
  log.append(msg)
  if settings.inspectAtHash.isEmpty then
    println(s"""\u001b[43;1m\u001b[30m${currentHash.take(8)}\u001b[0m $msg""")

def inspect(msg: String, settings: => Settings = readSettingsFromProjectFile()): Unit =
  for hash <- settings.inspectAtHash if currentHash.startsWith(hash) do
    println(s"""\u001b[43;1m\u001b[30mDEBUG:\u001b[0m $msg""")
