import PreludeTest.ExampleRunnableSpec
import adt._
import syntax._
import zio.test._
object InterviewSuggestionsSpec
    extends DefaultRunnableSpec
    with ExampleRunnableSpec {

  def spec = suite("InterviewSuggestionsSpec") {
    suite("interviewSuggestions")(
      testEg("EveryPossible Damn Thing")(
        {
          val i0 = Interviewer("i0")
          val c0 = Candidate("c0")

          eg(
            actual = InterviewSuggestions.suggestInterviews(i0 + c0),
            expected = Set()
          )
        }, {
          val s0 = Skill("s0")
          val i0 = Interviewer("i0", Set(s0))
          val r0 = Room("r0")
          val c0 = Candidate("c0", Set(s0))

          eg(
            actual = InterviewSuggestions.suggestInterviews(i0 + c0 + r0),
            expected = Set(
              Set(Interview(i0, c0, s0, r0))
            )
          )
        }, {
          val s1 = Skill("s1")
          val s0 = Skill("s0")
          val i0 = Interviewer("i0", Set(s0))
          val r0 = Room("r0")
          val c0 = Candidate("c0", Set(s1))

          eg(
            actual = InterviewSuggestions.suggestInterviews(i0 + c0 + r0),
            expected = Set()
          )
        }, {
          val s0 = Skill("s0")

          val i0 = Interviewer("i0", Set(s0))
          val i1 = Interviewer("i1", Set(s0))

          val r0 = Room("r0")

          val c0 = Candidate("c0", Set(s0))
          val c1 = Candidate("c1", Set(s0))

          eg(
            actual =
              InterviewSuggestions.suggestInterviews(i0 + i1 + c0 + c1 + r0),
            expected = Set(
              Set(Interview(i0, c0, s0, r0)),
              Set(Interview(i1, c0, s0, r0)),
              Set(Interview(i0, c1, s0, r0)),
              Set(Interview(i1, c1, s0, r0))
            )
          )
        }, {
          val s0 = Skill("s0")
          val s1 = Skill("s1")

          val i0 = Interviewer("i0", Set(s0))
          val i1 = Interviewer("i1", Set(s1))

          val r0 = Room("r0")

          val c0 = Candidate("c0", Set(s0))
          val c1 = Candidate("c1", Set(s1))

          eg(
            actual =
              InterviewSuggestions.suggestInterviews(i0 + i1 + c0 + c1 + r0),
            expected = Set(
              Set(Interview(i0, c0, s0, r0)),
              Set(Interview(i1, c1, s1, r0))
            )
          )
        }, {
          val s0 = Skill("s0")
          val s1 = Skill("s1")

          val i0 = Interviewer("i0", Set(s0))
          val i1 = Interviewer("i1", Set(s1))

          val r0 = Room("r0")
          val r1 = Room("r1")

          val c0 = Candidate("c0", Set(s0))
          val c1 = Candidate("c1", Set(s1))

          eg(
            actual =
              InterviewSuggestions.suggestInterviews(i0 + i1 + c0 + c1 + r0 + r1),
            expected = Set(
              Set(Interview(i0, c0, s0, r0), Interview(i1, c1, s1, r1)),
              Set(Interview(i0, c0, s0, r1), Interview(i1, c1, s1, r0))
            )
          )
        }
      )
    )
  }
}
