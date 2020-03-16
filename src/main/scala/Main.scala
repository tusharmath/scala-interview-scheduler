import zio.{UIO, ZIO}

object Main extends zio.App {
  def program                                                       = UIO(0)
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = program
}
