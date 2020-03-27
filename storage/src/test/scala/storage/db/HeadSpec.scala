package storage.db

import storage.db.DBNode.Digest
import storage.db.internal.TestRocksDB
import zio.random.Random
import zio.test.Assertion._
import zio.test.{DefaultRunnableSpec, _}

object HeadSpec extends DefaultRunnableSpec {
  override def spec = {
    suite("Head") {
      def randomDigest: Gen[Random, Digest] =
        for {
          byte <- Gen.byte(0.toByte, 10.toByte)
        } yield Digest(byte)

      testM("get / set") {
        checkM(randomDigest) { digest =>
          for {
            _      <- Head.set(digest)
            actual <- Head.get
          } yield assert(actual)(isSome(equalTo(digest)))
        }

      }
    }
  }.provideCustomLayer(TestRocksDB.live)
}
