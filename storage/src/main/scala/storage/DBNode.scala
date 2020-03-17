package db

import java.nio.ByteBuffer

import boopickle.Default.{Pickle, Unpickle, _}
import zio.{Task, UIO, ZIO}

sealed trait DBNode
object DBNode {

  case class Digest(bytes: Array[Byte])

  case class Root(content: Array[Byte])                        extends DBNode
  case class Commit(content: Array[Byte])                     extends DBNode
  case class Branch(key: Array[Byte])                            extends DBNode

  def digest(node: DBNode): UIO[Digest] =
    UIO(Digest(Array(node.hashCode().toByte)))
  def deserialize(bytes: Array[Byte]): Task[DBNode] = {
    ZIO.fromTry(Unpickle[DBNode].tryFromBytes(ByteBuffer.wrap(bytes)))
  }
  def serialize(node: DBNode): UIO[Array[Byte]] = {
    UIO(Pickle.intoBytes(node).array())
  }
}
