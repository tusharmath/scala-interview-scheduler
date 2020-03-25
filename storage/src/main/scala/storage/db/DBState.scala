package storage.db

import zio.rocksdb.RocksDB
import zio.{UIO, ZIO, rocksdb}

object DBState {
  def put(node: dbNode.DBNode): ZIO[RocksDB, Throwable, dbNode.Digest] =
    for {
      digest <- node.digest
      value  <- node.serialize
      _      <- rocksdb.put(Array(digest.byte), value.toArray)
    } yield digest

  def get(
      digest: dbNode.Digest
  ): ZIO[RocksDB, Throwable, Option[dbNode.DBNode]] =
    for {
      oBytes <- rocksdb.get(Array(digest.byte))
      node <- oBytes match {
        case Some(value) =>
          dbNode
            .deserialize(value.toList)
            .map(node => Some(node))
        case None => UIO(None)
      }
    } yield node
}
