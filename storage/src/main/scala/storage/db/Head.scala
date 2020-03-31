package storage.db

import zio.rocksdb.RocksDB
import zio.{RIO, ZIO, rocksdb}

/**
 * Represents the HEAD of the blockchain
 */
object Head {
  private lazy val HEAD_KEY = "HEAD".getBytes()

  /**
    * Sets the provided digest as the new HEAD.
    * @param digest
    * @return
    */
  def set(digest: Digest): RIO[RocksDB, Unit] = {
    rocksdb.put(HEAD_KEY, digest.serialize)
  }

  /**
   * Returns the head of the block chain
   * @return
   */
  def get: ZIO[RocksDB, Throwable, Option[Digest]] =
    for {
      maybeBytes <- rocksdb.get(HEAD_KEY)
      maybeDigest <- maybeBytes match {
        case Some(bytes) => Digest.deserialize(bytes).map(Some(_))
        case None        => ZIO.none
      }
    } yield maybeDigest
}
