package storage.db

import storage.db.internal.TestRocksDB
import zio.test.Assertion._
import zio.test._

object DBNodeSpec extends DefaultRunnableSpec {

  override def spec = {
    suite("DBNodeSpec")(
      testM("get/put") {
        val root = DBNode("ABC")
        for {
          digest <- root.write
          node   <- DBNode.read(digest)
        } yield {
          assert(node)(isSome(equalTo(root)))
        }
      },
      testM("read") {
        for {
          node <- DBNode.read("XYZ")
        } yield {
          assert(node)(isNone)
        }
      },
      testM("setHeadTo") {
        val node = DBNode("ABC")
        for {
          digest <- node.digest
          actual <- DBNode.setHeadTo(node)
        } yield {
          assert(actual)(equalTo(digest))
        }
      },
      test("headKey") {
        val actual = DBNode.headKey.toList
        assert(actual)(equalTo(List[Byte](72, 69, 65, 68)))
      },
      testM("getHeadDigest") {
        val node = DBNode("ABC")
        for {
          digest <- node.digest
          _      <- DBNode.setHeadTo(node)
          actual <- DBNode.getHeadDigest
        } yield {
          assert(actual)(isSome(equalTo(digest)))
        }
      },
      testM("getHeadValue") {
        val node = DBNode("ABC")
        for {
          _      <- DBNode.setHeadTo(node)
          actual <- DBNode.getHeadValue
        } yield {
          assert(actual)(isSome(equalTo(node)))
        }
      }
    ).provideCustomLayerShared(TestRocksDB.live)
  }
}
