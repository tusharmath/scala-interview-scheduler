object FindByTypeSyntax {
  import Syntax._

  import scala.reflect.ClassTag

  class FindByTypeOps(iterable: Iterable[Any]) {
    def findByType[T: ClassTag]: Option[T] =
      for {
        item <- iterable.find(_.as[T].nonEmpty)
      } yield item.asInstanceOf[T]
  }

  trait FindByTypeSyntax {
    implicit def findByTypeSyntax(iterable: Iterable[Any]): FindByTypeOps = {
      new FindByTypeOps(iterable)
    }
  }
}
