package internal

object TraversableOnceToString {
  def toString(A: Any, oSpace: String = ""): String = {
    val space = oSpace + "  "
    A match {
      case k: TraversableOnce[_] =>
        val set = k.map(ii => toString(ii, space + 1))
        val str = set.mkString(s",\n")
        val tag = k.getClass.getSimpleName.replaceAll("[0-9]", "")
        s"""${space}${tag}(\n${str}\n${space})"""
      case _ => space + A.toString
    }
  }
}
