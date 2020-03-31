package storage.db.internal

import storage.db.{DBNode, Digest}
import zio.random.Random
import zio.test.{Gen, Sized}

object StorageGen {
  def byte: Gen[Random, Byte] = Gen.byte(Byte.MinValue, Byte.MaxValue)

  def byteArray: Gen[Random with Sized, Array[Byte]] = {
    Gen.listOf(byte).map(_.toArray)
  }

  def dbNode: Gen[Random with Sized, DBNode] =
    for {
      content  <- byteArray
      previous <- Gen.option(digest)
      seq      <- Gen.long(0, Long.MaxValue)
    } yield DBNode(content, seq, previous)

  def digest: Gen[Random, Digest] = Gen.int(0, 10).map(Digest(_))

  def byteArrayList: Gen[Random with Sized, List[Array[Byte]]] = Gen.listOf {
    byteArray
  }
}
