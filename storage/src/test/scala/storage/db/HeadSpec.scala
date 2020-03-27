package storage.db

import storage.db.internal.{StorageGen, TestRocksDB}
import zio.test.Assertion._
import zio.test.{DefaultRunnableSpec, _}

object HeadSpec extends DefaultRunnableSpec {
  override def spec = {
    suite("Head") {
      testM("get / set") {
        checkM(StorageGen.digestGen) { digest =>
          for {
            _      <- Head.set(digest)
            actual <- Head.get
          } yield assert(actual)(isSome(equalTo(digest)))
        }

      }
    }
  }.provideCustomLayer(TestRocksDB.live)
}
