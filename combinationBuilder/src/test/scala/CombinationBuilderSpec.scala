import CombinationBuilder._
import zio.test.{DefaultRunnableSpec, _}

import scala.collection.immutable.Set

object CombinationBuilderSpec
    extends DefaultRunnableSpec
    with ExampleRunnableSpec {
  private abstract class LRes(val kind: Int)   extends Resource
  private case class Interviewer(name: String) extends LRes(0)
  private case class Candidate(name: String)   extends LRes(1)
  private case class Room(name: String)        extends LRes(2)

  // Instances
  private val i0 = Interviewer("i0")
  private val i1 = Interviewer("i1")
  private val c0 = Candidate("c0")
  private val c1 = Candidate("c1")
  private val r0 = Room("r0")
  private val r1 = Room("r1")

  override def spec =
    suite("combination()")(
      testEg[CombinationSet]("examples")(
        eg(
          actual = combination(Set(i0, i1, c0)),
          expected = Set(
            Set(Set(i0, c0)),
            Set(Set(i1, c0))
          )
        ),
        eg(
          actual = combination(Set(i0, i1, c0)),
          expected = Set(
            Set(Set(i0, c0)),
            Set(Set(i1, c0))
          )
        ),
        eg(
          actual = combination(Set(i0, i1, c0, c1)),
          expected = Set(
            Set(Set(i0, c0), Set(i1, c1)),
            Set(Set(i1, c0), Set(i0, c1))
          )
        ),
        eg(
          actual = combination(Set(i0, i1, c0, r0)),
          expected = Set(
            Set(Set(i0, c0, r0)),
            Set(Set(i1, c0, r0))
          )
        ),
        eg(
          actual = combination(Set(i0, i1, c0, c1, r0)),
          expected = Set(
            Set(Set(i0, c0, r0)),
            Set(Set(i1, c0, r0)),
            Set(Set(i0, c1, r0)),
            Set(Set(i1, c1, r0))
          )
        ),
        eg(
          actual = combination(Set(i0, c0, r0, r1)),
          expected = Set(
            Set(Set(i0, c0, r0)),
            Set(Set(i0, c0, r1))
          )
        )
      )
    )
}
