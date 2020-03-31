package storage.db

import java.nio.ByteBuffer

import boopickle.Default._
import zio.rocksdb.RocksDB
import zio.{Task, UIO, ZIO, rocksdb}
import scala.util.hashing.MurmurHash3

/**
  * Represents a node in the blockchain
  * @param content
  * @param seq
  * @param previous
  */
case class DBNode(
    content: Array[Byte],
    seq: Long,
    previous: Option[Digest] = None
) {

  /**
    * @see {{DBNode.digest}}
    * @return
    */
  def digest: Digest = DBNode.digest(this)

  /**
    * @see {{DBNode.serialize}}
    * @return
    */
  def serialize: Array[Byte] = DBNode.serialize(this)

  /**
    * @see {{DBNode.put}}
    * @return
    */
  def put: ZIO[RocksDB, Throwable, Digest] = DBNode.put(this)

  /**
    * Compares the equality of two nodes.
    * @param node
    * @return
    */
  override def equals(node: Any): Boolean = {
    node match {
      case n: DBNode => n.digest.code == digest.code
      case _         => super.equals(node)
    }
  }
}

object DBNode {

  /**
    * Generates a digest for the node.
    * @param node
    * @return
    */
  def digest(node: DBNode): Digest = Digest {
    MurmurHash3.bytesHash(node.serialize)
  }

  /**
    * Serializes the node to be saved into the db in future
    * @param node
    * @return
    */
  def serialize(node: DBNode): Array[Byte] = {
    Pickle.intoBytes(node).array()
  }

  /**
    * Inserts the node into the db.
    * Unlike `commit` it will not append it to the chain.
    * @param node
    * @return
    */
  def put(node: DBNode): ZIO[RocksDB, Throwable, Digest] = {
    for {
      _ <- rocksdb.put(node.digest.serialize, node.serialize)
    } yield node.digest
  }

  /**
    * Tries to find a node with the provided digest.
    * @param digest
    * @return
    */
  def get(digest: Digest): ZIO[RocksDB, Throwable, Option[DBNode]] = {
    for {
      vBytes <- rocksdb.get(digest.serialize)
      node <- vBytes match {
        case Some(value) => deserialize(value).map(Some(_))
        case None        => UIO(None)
      }
    } yield node
  }

  /**
    * Deserializes the given bytes into a Node
    * @param bytes
    * @return
    */
  def deserialize(bytes: Array[Byte]): Task[DBNode] = {
    ZIO.fromTry(Unpickle[DBNode].tryFromBytes(ByteBuffer.wrap(bytes)))
  }

  /**
    * Commits the provided bytes to the block
    * @param bytes
    * @return
    */
  def commit(bytes: Array[Byte]): ZIO[RocksDB, Throwable, Digest] =
    for {
      oHeadDigest <- Head.get
      oHeadNode   <- oHeadDigest.map(_.get).getOrElse(ZIO.none)
      seq = oHeadNode.map(_.seq).getOrElse(-1L)
      digest <- DBNode(bytes, seq + 1, oHeadDigest).put
      _      <- Head.set(digest)
    } yield digest

  /**
    * Tries to load the given number of node from the blockchain.
    * @param n
    * @return
    */
  def loadN(n: Int): ZIO[RocksDB, Throwable, List[Array[Byte]]] = {
    def itar(
        list: List[Array[Byte]],
        digest: Digest,
        count: Int
    ): ZIO[RocksDB, Throwable, List[Array[Byte]]] = {
      if (count == 0) UIO {
        list
      } else
        for {
          node <- digest.get
          k <- node match {
            case Some(dbNode) =>
              val nList = dbNode.content :: list
              dbNode.previous match {
                case Some(previous) => itar(nList, previous, count - 1)
                case None           => UIO(nList)
              }
            case None => UIO(list)
          }
        } yield k
    }

    for {
      digest <- Head.get
      k <- digest match {
        case Some(value) => itar(List.empty, value, n)
        case None        => UIO(Nil)
      }
    } yield k
  }
}
