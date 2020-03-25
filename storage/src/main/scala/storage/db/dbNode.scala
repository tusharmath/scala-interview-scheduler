package storage.db

import java.nio.ByteBuffer

import boopickle.Default._
import zio.rocksdb.RocksDB
import zio.{Task, UIO, ZIO, rocksdb}

object dbNode {
  case class Digest(byte: Byte)

  case class DBNode(val previous: Option[Digest], val content: List[Byte]) {

    def digest: UIO[Digest] = {
      UIO(Digest(content.hashCode().toByte))
    }
    def serialize: UIO[List[Byte]] = {
      UIO(Pickle.intoBytes(this).array().toList)
    }

    def write: ZIO[RocksDB, Throwable, Digest] = dbNode.write(this)
  }

  def deserialize(bytes: List[Byte]): Task[DBNode] = {
    ZIO.fromTry(Unpickle[DBNode].tryFromBytes(ByteBuffer.wrap(bytes.toArray)))
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
}