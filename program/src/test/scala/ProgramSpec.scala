import InterviewADT.{Candidate, Interviewer, Skill}
import zio.test.Assertion._
import zio.test.{DefaultRunnableSpec, _}
import zio.test.environment.TestConsole

object ProgramSpec extends DefaultRunnableSpec {
  def spec =
    suite("MainSpec")(
      suite("getSkillSet()")(
        testM("should return a set of skills") {

          for {
            _      <- TestConsole.feedLines("aa bb cc")
            actual <- Program.getSkillSet
            expected = Set(
              Skill("aa"),
              Skill("bb"),
              Skill("cc")
            )
          } yield assert(actual)(equalTo(expected))

        },
        testM("should trim extra space") {

          for {
            _      <- TestConsole.feedLines(" aa bb   cc   ")
            actual <- Program.getSkillSet
            expected = Set(
              Skill("aa"),
              Skill("bb"),
              Skill("cc")
            )
          } yield assert(actual)(equalTo(expected))

        }
      ),
      suite("printSkills")(
        testM("should be formatted") {
          for {
            _      <- Program.printSkills(Set(Skill("A"), Skill("B")))
            actual <- TestConsole.output
            expected = Vector("Skills:\n", "1.  A\n2.  B\n")

          } yield assert(actual)(equalTo(expected))
        }
      ),
      suite("addInterviewer")(
        testM("should create an interviewer") {
          for {
            _      <- TestConsole.feedLines("Tushar", "n", "y")
            actual <- Program.addInterviewer(Set(Skill("A"), Skill("B")))
            expected = Interviewer("Tushar", Set(Skill("B")))
          } yield {

            assert(actual)(equalTo(expected))
          }
        }
      ),
      suite("addCandidate")(
        testM("should create a candidate") {
          for {
            _      <- TestConsole.feedLines("Tushar", "n", "y")
            actual <- Program.addCandidate(Set(Skill("A"), Skill("B")))
            expected = Candidate("Tushar", Set(Skill("B")))
          } yield {

            assert(actual)(equalTo(expected))
          }
        }
      )
    )
}
