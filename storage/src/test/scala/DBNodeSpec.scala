import java.nio.file.{Files, Path}

import storage.db.DBNode._
import zio._
import zio.test.Assertion._
import zio.test._

import scala.reflect.io
object DBNodeSpec extends DefaultRunnableSpec {

  private def deletePath(path: Path): UIO[Boolean] =
    IO {
      new io.Directory(path.toFile).deleteRecursively()
    } <> UIO(false)

  private def managedDirectory =
    Task(Files.createTempDirectory("storage"))
      .toManaged(deletePath)

  private def testDB =
    (for {
      dir     <- managedDirectory
      service <- rocksdb.Live.open(dir.toAbsolutePath.toString)
    } yield service).toLayer.mapError(TestFailure.die)

  override def spec = {
    suite("DBNodeSpec")(
      testM("get/put") {
        val root = Root("ABC".getBytes().toList)
        for {
          digest <- root.write
          node   <- read(digest)
        } yield {

          assert(node)(isSome(equalTo(root)))
        }
      }
    ).provideCustomLayerShared(testDB)
  }
}