package storage.db.internal

import storage.db.DBNode
import zio.random.Random
import zio.test.{Gen, Sized}

object StorageGen {

  def byteGen: Gen[Random, Byte] = Gen.byte(Byte.MinValue, Byte.MaxValue)
  def byteArrayGen: Gen[Random with Sized, Array[Byte]] = {
    Gen.listOf(byteGen).map(_.toArray)
  }
  def dbNodeGen: Gen[Random with Sized, DBNode] = {
    byteArrayGen.map(DBNode(None, _))
  }
  def digestGen: Gen[Random, DBNode.Digest] = byteGen.map(DBNode.Digest)

}
