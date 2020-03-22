import CombinationBuilder.{Resource, combination, combinationWith}
import PreludeTest.ExampleRunnableSpec
import zio.test.{DefaultRunnableSpec, suite}

import scala.collection.immutable.Set

object CombinationBuilderSpec
    extends DefaultRunnableSpec
    with ExampleRunnableSpec {

  private sealed abstract class LRes(val kind: Int) extends Resource
  private case class Interviewer(name: String, skills: Set[String] = Set.empty)
      extends LRes(0)
  private case class Candidate(name: String, skills: Set[String] = Set.empty)
      extends LRes(1)
  private case class Room(name: String) extends LRes(2)

  // Instances

  override def spec =
    suite("CombinationBuilder")(
      suite("combination()") {
        val i0 = Interviewer("i0")
        val i1 = Interviewer("i1")
        val c0 = Candidate("c0")
        val c1 = Candidate("c1")
        val r0 = Room("r0")
        val r1 = Room("r1")

        testEg("cross product")(
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
      },
      suite("combinationWith()") {
        val i0 = Interviewer("i0", Set("s0"))
        val i1 = Interviewer("i1", Set("s1"))
        val c0 = Candidate("c0", Set("s0"))

        testEg("cross product")(
          eg(
            actual = combinationWith(Set(i0, i1, c0))(_ => false),
            expected = Set.empty
          ),
          eg(
            actual = combinationWith(Set(i0, i1, c0))(combination => {
              (for {
                i <- combination.findByType[Interviewer]
                c <- combination.findByType[Candidate]
              } yield c.skills.intersect(i.skills).nonEmpty).getOrElse(false)
            }),
            expected = Set(Set(Set(i0, c0)))
          )
        )
      }
    )

}
