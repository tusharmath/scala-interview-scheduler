package storage.db

import storage.db.internal.{StorageGen, TestRocksDB}
import zio.test.Assertion.{equalTo, isSome, not}
import zio.test._

object DigestSpec extends DefaultRunnableSpec {
  override def spec = {
    def properties =
      List(
        testM("dissimilar nodes should have different digests") {
          check(StorageGen.dbNode, StorageGen.dbNode) { (A, B) =>
            {
              assert(A.digest)(not(equalTo(B.digest)))
            }
          }
        },
        testM("should produce same digest serialize/deserialize") {
          checkM(StorageGen.dbNode)(node => {
            for {
              node0 <- DBNode.deserialize(node.serialize)
            } yield assert(node.digest)(equalTo(node0.digest))
          })
        },
        testM("putting should return the same digest") {
          checkM(StorageGen.dbNode) { node0 =>
            for {
              digest0 <- node0.put
            } yield {
              assert(node0.digest)(equalTo(digest0))
            }
          }
        },
        testM("putting and getting the digest should be same") {
          checkM(StorageGen.dbNode) { node0 =>
            for {
              digest0 <- node0.put
              node1   <- digest0.get
              digest1 = node1.map(_.digest)
            } yield {
              assert(digest1)(isSome(equalTo(digest0)))
            }
          }
        }
      )
    suite("DigestSpec")(
      suite("memory")(properties: _*)
        .provideCustomLayerShared(TestRocksDB.memory)
    )
  }
}
