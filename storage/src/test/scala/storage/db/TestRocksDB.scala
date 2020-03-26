package storage.db

import java.nio.file.{Files, Path}

import zio._
import zio.rocksdb.RocksDB
import zio.test.TestFailure

import scala.reflect.io

object TestRocksDB {
  def live: ZLayer[Any, TestFailure[Nothing], Has[RocksDB.Service]] =
    (for {
      dir     <- managedDirectory
      service <- rocksdb.Live.open(dir.toAbsolutePath.toString)
    } yield service).toLayer.mapError(TestFailure.die)

  private def managedDirectory =
    Task(Files.createTempDirectory("storage"))
      .toManaged(deletePath)

  private def deletePath(path: Path): UIO[Boolean] =
    IO {
      new io.Directory(path.toFile).deleteRecursively()
    } <> UIO(false)
}
