package storage.db

import storage.db.internal.{StorageGen, TestRocksDB}
import zio.ZIO
import zio.rocksdb.RocksDB
import zio.test.Assertion.{equalTo, isSome, not, isTrue}
import zio.test.TestAspect._
import zio.test._

import scala.util.hashing.MurmurHash3

object DBNodeSpec extends DefaultRunnableSpec {
  private val properties = suite("properties")(
    suite("commit()")(
      testM("random sequence of bytes") {
        checkM(
          StorageGen.byteArray,
          StorageGen.byteArray,
          StorageGen.byteArray
        )((A, B, C) => {
          for {
            _ <- DBNode.commit(A)
            _ <- DBNode.commit(B)
            _ <- DBNode.commit(C)

            list <- DBNode.loadN(3)
          } yield {
            val actual   = list.map(MurmurHash3.bytesHash)
            val expected = List(A, B, C).map(MurmurHash3.bytesHash)
            assert(actual)(equalTo(expected))
          }
        })
      },
      testM("random bytes") {
        checkM(StorageGen.byteArray)(A => {
          for {
            digest <- DBNode.commit(A)
            node   <- digest.get
            bytes = node.map(_.content.toList)
          } yield {
            assert(bytes)(isSome(equalTo(A.toList)))
          }
        })
      },
      testM("should set initial sequence to 0") {
        checkM(StorageGen.byteArray)(a => {
          (for {
            digest <- DBNode.commit(a)
            node   <- digest.get
            seq = node.map(_.seq)
          } yield {
            assert(seq)(isSome(equalTo(0L)))
          }).provideCustomLayer(TestRocksDB.memory)
        })
      },
      testM("should set sequence in incremental order") {
        checkM(
          StorageGen.byteArray,
          StorageGen.byteArray,
          StorageGen.byteArray
        )((A, B, C) => {
          for {
            aNode <- DBNode.commit(A) >>= (_.get)
            bNode <- DBNode.commit(B) >>= (_.get)
            cNode <- DBNode.commit(C) >>= (_.get)
            actual = aNode.map(_.seq) ++ bNode.map(_.seq) ++ cNode.map(_.seq)
          } yield {
            val x = actual match {
              case List(a, b, c) => a + c == b * 2
              case _             => false
            }
            assert(x)(isTrue)
          }
        })
      },
      testM("should set HEAD") {
        val A = Array(1.toByte)
        for {
          digest      <- DBNode.commit(A)
          maybeDigest <- Head.get
        } yield {
          assert(maybeDigest)(isSome(equalTo(digest)))
        }
      },
      testM("should return the digest") {
        checkM(StorageGen.byteArray)(A => {
          for {
            digest0 <- DBNode.commit(A)
            digest1 <- Head.get
          } yield {
            assert(digest1)(isSome(equalTo(digest0)))
          }
        })
      }
    )
  )

  override def spec = {
    suite("DBNodeSpec")(
      suite("memory")(properties.provideCustomLayer(TestRocksDB.memory)),
      suite("disk")(properties.provideCustomLayerShared(TestRocksDB.disk)) @@ ignore
    )
  }
}
