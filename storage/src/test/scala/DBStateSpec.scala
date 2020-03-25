import java.nio.file.{Files, Path}

import storage.db.DBState
import storage.db.dbNode.Root
import zio._
import zio.test.Assertion._
import zio.test._

import scala.reflect.io
object DBStateSpec extends DefaultRunnableSpec {

  private def deletePath(path: Path): UIO[Boolean] =
    IO {
      new io.Directory(path.toFile).deleteRecursively()
    } <> UIO(false)

  private def managedDirectory =
    Task(Files.createTempDirectory("zio-rocksdb"))
      .toManaged(deletePath)

  private def testDB =
    (for {
      dir     <- managedDirectory
      service <- rocksdb.Live.open(dir.toAbsolutePath.toString)
    } yield service).toLayer.mapError(TestFailure.die)

  override def spec = {
    suite("DBStateSpec")(
      testM("get/put") {
        val root = Root("ABC".getBytes().toList)
        for {
          _      <- DBState.put(root)
          digest <- root.digest
          node   <- DBState.get(digest)
        } yield {

          assert(node)(isSome(equalTo(root)))
        }
      }
    ).provideCustomLayerShared(testDB)
  }
}
