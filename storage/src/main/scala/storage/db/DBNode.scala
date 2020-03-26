package storage.db

import java.nio.ByteBuffer

import boopickle.Default._
import storage.db.DBNode.Digest
import zio.rocksdb.RocksDB
import zio.{Task, UIO, ZIO, rocksdb}
case class DBNode(previous: Option[Digest], content: List[Byte]) {
  def digest: UIO[Digest]                    = DBNode.digest(content)
  def serialize: UIO[List[Byte]]             = DBNode.serialize(this)
  def write: ZIO[RocksDB, Throwable, Digest] = DBNode.write(this)
}

object DBNode {
  def apply(content: List[Byte]): DBNode = new DBNode(None, content)
  def apply(previous: Digest, content: List[Byte]): DBNode = {
    new DBNode(Some(previous), content)
  }

  def digest(content: List[Byte]) = {
    UIO(Digest(content.hashCode().toByte))
  }

  def serialize(node: DBNode): UIO[List[Byte]] = {
    UIO(Pickle.intoBytes(node).array().toList)
  }

  def write(node: DBNode): ZIO[RocksDB, Throwable, Digest] =
    for {
      digest <- node.digest
      value  <- node.serialize
      _      <- rocksdb.put(Array(digest.byte), value.toArray)
    } yield digest

  def read(
      digest: Digest
  ): ZIO[RocksDB, Throwable, Option[DBNode]] =
    for {
      oBytes <- rocksdb.get(Array(digest.byte))
      node <- oBytes match {
        case Some(value) => deserialize(value.toList).map(node => Some(node))
        case None        => UIO(None)
      }
    } yield node

  def deserialize(bytes: List[Byte]): Task[DBNode] = {
    ZIO.fromTry(Unpickle[DBNode].tryFromBytes(ByteBuffer.wrap(bytes.toArray)))
  }

  case class Digest(byte: Byte)

}
