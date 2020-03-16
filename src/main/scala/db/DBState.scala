package db

import db.DBNode.{Branch, Commit, Digest}
import zio.rocksdb.RocksDB
import zio.{rocksdb, _}

trait RocksDBSerializer[A] {
  def toByte: Array[Byte]
}
object DBState {

  def insertNode(head: Branch,
                 content: Commit): ZIO[RocksDB, Throwable, Digest] =
    for {
      dContent <- DBNode.digest(head)
      bContent <- DBNode.serialize(content)
      bHead    <- DBNode.serialize(head)
      _        <- rocksdb.put(dContent.bytes, bContent)
      _        <- rocksdb.put(bHead, bContent)
    } yield dContent

  def get(digest: Digest): ZIO[RocksDB, Throwable, Option[DBNode]] =
    for {
      bytes <- rocksdb.get(digest.bytes)
      node <- bytes match {
        case Some(value) => DBNode.deserialize(value).map(Some(_))
        case None        => UIO(None)
      }
    } yield node

}
