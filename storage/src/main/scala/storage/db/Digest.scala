package storage.db

import java.nio.ByteBuffer

import boopickle.Default.{Pickle, Unpickle, _}
import zio.rocksdb.RocksDB
import zio.{Task, ZIO}

/**
 * Encodes the "digest" of a given blob of data.
 * @param code
 */
case class Digest(code: Int) {
  /**
   * Returns the node for this digest from the block chain.
   * @return
   */
  def get: ZIO[RocksDB, Throwable, Option[DBNode]] = DBNode.get(this)

  /**
   * Checks if this digest exists in the blockchain
   * @return
   */
  def exists: ZIO[RocksDB, Throwable, Boolean] = get.map(_.isDefined)

  /**
   * Serializes the digest to be saved as a key in the block chain.
   * @return
   */
  def serialize: Array[Byte] = Pickle.intoBytes(code).array()

}

object Digest {
  /**
   * Tries to convert bytes to a Digest.
   * @param bytes
   * @return
   */
  def deserialize(bytes: Array[Byte]): Task[Digest] = {
    ZIO
      .fromTry(Unpickle[Int].tryFromBytes(ByteBuffer.wrap(bytes)))
      .map(Digest(_))
  }
}
