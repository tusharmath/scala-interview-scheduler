import storage.db.DBNode
import storage.db.DBNode._
import zio.test.Assertion._
import zio.test._
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
