import scala.reflect.ClassTag

/**
  * FTW!
  */
object Prelude {
  def as[A: ClassTag](any: Any): Option[A] = any match {
    case a: A => Some(a)
    case _    => None
  }

  implicit class AsOp(any: Any) {
    def as[A: ClassTag] = Prelude.as(any)
  }

  implicit class SetOps(iterable: Iterable[Any]) {
    def findByType[T: ClassTag]: Option[T] =
      for {
        item <- iterable.find(_.as[T].nonEmpty)
      } yield item.asInstanceOf[T]
  }
}
