import scala.reflect.ClassTag

object AsSyntax {
  class AsOp(any: Any) {
    def as[A: ClassTag] = any match {
      case a: A => Some(a)
      case _    => None
    }
  }

  trait AsSyntax {
    implicit def asSyntax(any: Any): AsOp = new AsOp(any)
  }
}
