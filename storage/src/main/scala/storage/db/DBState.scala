package storage.db

import storage.db.DBNode._
import zio.rocksdb.RocksDB
import zio.{ZIO, rocksdb}

object DBState {
  def put(node: DBNode): ZIO[RocksDB, Throwable, Digest] =
    for {
      digest <- node.digest
      value  <- node.serialize
      _      <- rocksdb.put(digest.bytes, value)
    } yield digest
}
