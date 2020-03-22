package syntax.general

import scala.reflect.ClassTag

trait FindByTypeSyntax {
  implicit class FindByTypeOps(iterable: Iterable[Any]) {
    def findByType[T: ClassTag]: Option[T] =
      for {
        item <- iterable.find(_.as[T].nonEmpty)
      } yield item.asInstanceOf[T]
  }
}
