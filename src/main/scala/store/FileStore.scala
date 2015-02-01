package store

import java.io.{File, RandomAccessFile}
import java.nio.{MappedByteBuffer, CharBuffer}
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.security.MessageDigest

import scala.collection.mutable
import scala.util.Try

class FileStore(dir: String) extends Store {

  val charset = Charset.forName("utf8")

  private val files = mutable.HashMap.empty[String, MappedFile]
  loadKeys()

  override def get(key: String): Option[String] = {
    files.get(hash(key)).map(_.read)
  }

  override def add(key: String, value: String): Unit = {
    val f = files.getOrElseUpdate(hash(key), file(key))
    f.write(value)
  }

  private def file(key: String): MappedFile = {
    val path = s"$dir${File.separator}${hash(key)}"
    MappedFile(path)
  }

  private def hash(key: String): String = {
    val digest = MessageDigest.getInstance("MD5").digest(key.getBytes(charset))
    new java.math.BigInteger(1, digest).toString(16)
  }

  private def loadKeys(): Unit = {
    val f = new File(dir)
    if (f.exists() && !f.isDirectory) throw new Exception(s"Invalid dir $dir")
    else f.mkdirs()

    for { c <- f.listFiles } {
      files(c.getName) = MappedFile(c.getAbsolutePath)
    }
  }

  private case class MappedFile(path: String) {

    private val file = new File(path)

    private val randomFile = new RandomAccessFile(file, "rw")

    private val channel = randomFile.getChannel

    private var map: MappedByteBuffer = _
    loadMap()

    def read(): String = {
      charset.newDecoder().decode(map.asReadOnlyBuffer()).toString
    }

    def write(value: String): Unit = {
      val encoder = charset.newEncoder()
      channel.position(0)
      channel.write(encoder.encode(CharBuffer.wrap(value)))
      loadMap()
    }

    def close() = {
      Try(channel.close())
      Try(randomFile.close())
    }

    private def loadMap() = {
      map = channel.map(FileChannel.MapMode.PRIVATE, 0, channel.size())
    }

  }

}

