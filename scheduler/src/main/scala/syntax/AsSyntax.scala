package syntax

import scala.reflect.ClassTag

trait AsSyntax {
  implicit class AsOp(any: Any) {
    def as[A: ClassTag]: Option[A] = any match {
      case a: A => Some(a)
      case _    => None
    }
  }
}
