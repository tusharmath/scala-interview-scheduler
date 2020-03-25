package storage.db

import java.nio.ByteBuffer

import boopickle.Default._
import zio.{Task, UIO, ZIO}

package object dbNode {
  case class Digest(byte: Byte)

  sealed trait DBNode {
    val content: List[Byte]
    def digest: UIO[Digest] = {
      UIO(Digest(content.hashCode().toByte))
    }
    def serialize: UIO[List[Byte]] = {
      UIO(Pickle.intoBytes(this).array().toList)
    }
  }

  def deserialize(bytes: List[Byte]): Task[DBNode] = {
    ZIO.fromTry(Unpickle[DBNode].tryFromBytes(ByteBuffer.wrap(bytes.toArray)))
  }

  case class Root[T](content: List[Byte]) extends DBNode {
    override def toString: String = s"Root($content)"
  }
  case class Commit(content: List[Byte]) extends DBNode
  case class Branch(content: List[Byte]) extends DBNode
}
