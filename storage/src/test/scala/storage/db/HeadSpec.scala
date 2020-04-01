package storage.db

import storage.db.internal.{StorageGen, TestRocksDB}
import zio.test.Assertion._
import zio.test.{DefaultRunnableSpec, _}

object HeadSpec extends DefaultRunnableSpec {
  override def spec = {
    suite("HeadSpec")(
      suite("Memory")(properties: _*).provideCustomLayer(TestRocksDB.memory)
    )
  }

  private def properties = {
    List(testM("should set & get digests") {
      checkM(StorageGen.digest) { digest =>
        for {
          _      <- Head.set(digest)
          actual <- Head.get
        } yield assert(actual)(isSome(equalTo(digest)))
      }
    })
  }
}
