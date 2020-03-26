package storage.db

import storage.db.DBNode.read
import storage.db.internal.TestRocksDB
import zio.test.Assertion.{equalTo, isSome}
import zio.test.{DefaultRunnableSpec, assert, suite, testM}

object DBNodeSpec extends DefaultRunnableSpec {

  override def spec = {
    suite("DBNodeSpec")(
      testM("get/put") {
        val root = DBNode(None, "ABC".getBytes().toList)
        for {
          digest <- root.write
          node   <- read(digest)
        } yield {

          assert(node)(isSome(equalTo(root)))
        }
      }
    ).provideCustomLayerShared(TestRocksDB.live)
  }
}
