import InterviewADT._
import zio.test.Assertion._
import zio.test._

object InterviewSuggestionsSpec extends DefaultRunnableSpec {
  import EventAvailabilityOps._

  def spec = suite("InterviewSuggestionsSpec") {
    suite("interviewSuggestions")(
      test("should remove resources") {

        val s0 = Skill("s0")
        val s1 = Skill("s1")

        val i0 = Interviewer("i0", Set(s0))
        val i1 = Interviewer("i1", Set(s0, s1))

        val c0 = Candidate("c0", Set(s0))
        val c1 = Candidate("c1", Set(s1))

        val r0 = Room("r0")
        val r1 = Room("r1")

        val rooms = Set(r0, r1)
        val candidates = Set(c0, c1)
        val interviewers = Set(i0, i1)

        val availability = EventAvailability(
          interviewers = interviewers,
          candidates = candidates,
          rooms = rooms
        )

        val actual = availability.interviewSuggestions

        val expected = Set[Set[Interview]](
          Set(Interview(i1, c1, s1, r1), Interview(i0, c0, s0, r0)),
          Set(Interview(i0, c0, s0, r1), Interview(i1, c1, s1, r0))
        )

        assert(actual)(equalTo(expected))
      },
      test("should skip referral taking interviews") {
        val s0 = Skill("s0")

        val i0 = Interviewer("i0", Set(s0))
        val i1 = Interviewer("i1", Set(s0))

        val c0 = Candidate("c0", Set(s0))

        val r0 = Room("r0")
        val r1 = Room("r1")

        val rooms = Set(r0, r1)
        val candidates = Set(c0)
        val interviewers = Set(i0, i1)

        val availability = EventAvailability(
          interviewers = interviewers,
          candidates = candidates,
          rooms = rooms,
          referrals = Set((c0, i0))
        )

        val actual = availability.interviewSuggestions

        val expected = Set[Set[Interview]](
          Set(Interview(i1, c0, s0, r0)),
          Set(Interview(i1, c0, s0, r1))
        )

        assert(actual)(equalTo(expected))
      }
    )
  }
}
