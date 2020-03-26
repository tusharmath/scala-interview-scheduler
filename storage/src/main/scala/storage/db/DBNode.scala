package storage.db

import java.nio.ByteBuffer

import boopickle.Default._
import storage.db.DBNode.Digest
import zio.rocksdb.RocksDB
import zio.{Task, UIO, ZIO, rocksdb}

case class DBNode(previous: Option[Digest], content: List[Byte]) {
  def digest: UIO[Digest]                    = DBNode.digest(content)
  def serialize: UIO[Array[Byte]]            = DBNode.serialize(this)
  def write: ZIO[RocksDB, Throwable, Digest] = DBNode.write(this)
//  def commit: ZIO[RocksDB, Throwable, Digest] = DBNode.commit(this)
}

object DBNode {
  def apply(content: Array[Byte]): DBNode = new DBNode(None, content.toList)
  def apply(content: List[Byte]): DBNode  = new DBNode(None, content)
  def apply(content: String): DBNode =
    new DBNode(None, content.getBytes().toList)
  def apply(previous: Digest, content: List[Byte]): DBNode = {
    new DBNode(Some(previous), content)
  }

//  def commit(node: DBNode): ZIO[RocksDB, Throwable, Digest] = ???
  def getHeadDigest: ZIO[RocksDB, Throwable, Option[Digest]] =
    for {
      oNodeBytes <- rocksdb.get(headKey)
      byte       <- UIO(oNodeBytes.flatMap(_.headOption))
    } yield byte.map(Digest)

  def setHeadTo(node: DBNode): ZIO[RocksDB, Throwable, Digest] =
    for {
      nDigest <- node.digest
      _       <- rocksdb.put(headKey, Array(nDigest.byte))
    } yield nDigest

  def getHeadValue: ZIO[RocksDB, Throwable, Option[DBNode]] =
    for {
      oDigest <- getHeadDigest
      node <- oDigest match {
        case Some(value) => read(value)
        case None        => UIO(None)
      }
    } yield node
  def headKey: Array[Byte] = "HEAD".getBytes()

  def digest(content: List[Byte]): UIO[Digest] = {
    UIO(Digest(content.hashCode().toByte))
  }

  private def serialize(node: DBNode): UIO[Array[Byte]] = {
    UIO(Pickle.intoBytes(node).array())
  }

  def write(node: DBNode): ZIO[RocksDB, Throwable, Digest] = {
    for {
      digest <- node.digest
      value  <- node.serialize
      _      <- rocksdb.put(Array(digest.byte), value)
    } yield digest
  }

  def read(digest: Digest): ZIO[RocksDB, Throwable, Option[DBNode]] = {
    read(List(digest.byte))
  }
  def read(bytes: List[Byte]): ZIO[RocksDB, Throwable, Option[DBNode]] = {
    read(bytes.toArray)
  }
  def read(string: String): ZIO[RocksDB, Throwable, Option[DBNode]] = {
    read(string.getBytes())
  }
  def read(bytes: Array[Byte]): ZIO[RocksDB, Throwable, Option[DBNode]] = {
    for {
      vBytes <- rocksdb.get(bytes)
      node <- vBytes match {
        case Some(value) => deserialize(value).map(Some(_))
        case None        => UIO(None)
      }
    } yield node
  }

  private def deserialize(bytes: Array[Byte]): Task[DBNode] = {
    ZIO.fromTry(Unpickle[DBNode].tryFromBytes(ByteBuffer.wrap(bytes)))
  }

  case class Digest(byte: Byte)

}
