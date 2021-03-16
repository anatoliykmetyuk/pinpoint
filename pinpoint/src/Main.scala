package pinpoint

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter
import scala.collection.mutable.ListBuffer

@main def Main =
  trace("foo")
  inspect("AAA")
  trace("bar")
  inspect("BBB")
  trace("jar")

case class Settings(inspectAtHash: Option[String])
def readSettingsFromProjectFile() = Settings(
  inspectAtHash = Some("3858f")
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
    println(s"""\u001b[43;1m\u001b[30m${currentHash.take(5)}\u001b[0m $msg""")

def inspect(msg: String, settings: => Settings = readSettingsFromProjectFile()): Unit =
  for hash <- settings.inspectAtHash if currentHash.startsWith(hash) do
    println(s"""\u001b[43;1m\u001b[30mDEBUG:\u001b[0m $msg""")
