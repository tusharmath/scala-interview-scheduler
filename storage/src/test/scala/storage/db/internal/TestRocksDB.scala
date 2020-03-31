package storage.db.internal

import java.nio.file.{Files, Path}
import java.util.Date

import org.rocksdb.ColumnFamilyHandle
import zio._
import zio.rocksdb.RocksDB
import zio.test.TestFailure

import scala.collection.mutable
import scala.reflect.io
import scala.util.hashing.MurmurHash3

object TestRocksDB {
  case class MemoryDB() extends RocksDB.Service {
    private val memory = mutable.Map.empty[Int, Array[Byte]]
    override def put(key: Array[Byte], value: Array[Byte]): Task[Unit] =
      Task {
        memory.put(MurmurHash3.bytesHash(key), Array(value: _*))
      }

    override def get(key: Array[Byte]): Task[Option[Array[Byte]]] = Task {
      memory.get(MurmurHash3.bytesHash(key))
    }

    override def delete(key: Array[Byte]): Task[Unit] = ???

    override def delete(
        cfHandle: ColumnFamilyHandle,
        key: Array[Byte]
    ): Task[Unit] = ???

    override def get(
        cfHandle: ColumnFamilyHandle,
        key: Array[Byte]
    ): Task[Option[Array[Byte]]] = ???

    override def initialHandles: Task[List[ColumnFamilyHandle]] = ???

    override def multiGetAsList(
        keys: List[Array[Byte]]
    ): Task[List[Option[Array[Byte]]]] = ???

    override def multiGetAsList(
        handles: List[ColumnFamilyHandle],
        keys: List[Array[Byte]]
    ): Task[List[Option[Array[Byte]]]] = ???

    override def newIterator
        : stream.Stream[Throwable, (Array[Byte], Array[Byte])] = ???

    override def newIterator(
        cfHandle: ColumnFamilyHandle
    ): stream.Stream[Throwable, (Array[Byte], Array[Byte])] = ???

    override def newIterators(
        cfHandles: List[ColumnFamilyHandle]
    ): stream.Stream[
      Throwable,
      (
          ColumnFamilyHandle,
          stream.Stream[Throwable, (Array[Byte], Array[Byte])]
      )
    ] = ???

    override def put(
        cfHandle: ColumnFamilyHandle,
        key: Array[Byte],
        value: Array[Byte]
    ): Task[Unit] = ???

    def clear: UIO[Any] = UIO {
      memory.clear()
    }
  }

  def memory: ZLayer[Any, TestFailure[Nothing], RocksDB] =
    UIO { MemoryDB() }
      .toManaged(_.clear)
      .map(_.asInstanceOf[RocksDB.Service])
      .toLayer
      .mapError(TestFailure.die)

  def disk: ZLayer[Any, TestFailure[Nothing], RocksDB] =
    (for {
      dir     <- managedDirectory
      service <- rocksdb.Live.open(dir.toAbsolutePath.toString)
    } yield service).toLayer.mapError(TestFailure.die)

  private def managedDirectory =
    Task {
      Files.createTempDirectory("interview-scheduler-storage")
    }.toManaged(deletePath)

  private def deletePath(path: Path): UIO[Boolean] =
    IO {
      new io.Directory(path.toFile).deleteRecursively()
    } <> UIO(false)
}
