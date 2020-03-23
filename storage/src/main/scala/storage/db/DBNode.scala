package storage.db

import java.nio.ByteBuffer

import boopickle.Default._
import zio.{Task, UIO, ZIO}

package object DBNode {
  case class Digest(bytes: Array[Byte])

  sealed trait DBNode {
    val content: Array[Byte]
    def digest: UIO[Digest] = {
      UIO(Digest(Array(content.hashCode().toByte)))
    }
    def serialize: UIO[Array[Byte]] = {
      UIO(Pickle.intoBytes(this).array())
    }
  }

  def deserialize(bytes: Array[Byte]): Task[DBNode] = {
    ZIO.fromTry(Unpickle[DBNode].tryFromBytes(ByteBuffer.wrap(bytes)))
  }

  case class Root(val content: Array[Byte])   extends DBNode
  case class Commit(val content: Array[Byte]) extends DBNode
  case class Branch(val content: Array[Byte]) extends DBNode
}
