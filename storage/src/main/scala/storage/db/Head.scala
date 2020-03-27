package storage.db

import storage.db.DBNode.Digest
import zio.rocksdb.RocksDB
import zio.{UIO, ZIO, rocksdb}

object Head {
  private val HEAD_KEY = "HEAD".getBytes()
  def set(digest: Digest): ZIO[RocksDB, Throwable, Unit] = {
    rocksdb.put(HEAD_KEY, digest.getBytes)
  }

  def get: ZIO[RocksDB, Throwable, Option[Digest]] =
    for {
      oNodeBytes <- rocksdb.get(HEAD_KEY)
      byte       <- UIO(oNodeBytes.flatMap(_.headOption))
    } yield byte.map(Digest)
}
