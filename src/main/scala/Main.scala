import java.time.LocalDateTime

import scala.concurrent.duration._
import scala.language.postfixOps

object Main extends App {

  import InterviewADT._

  val s0 = Skill("s0")
  val s1 = Skill("s1")
  val availability = EventAvailability(
    interviewers = Set(
      Interviewer("i0", Set(s0, s1)),
      Interviewer("i1", Set(s1)),
    ),
    candidates = Set(
      Candidate("c0", Set(s0)),
      Candidate("c1", Set(s1)),
    ),
    interviewDuration = 45 minutes,
    interviewStartTime = LocalDateTime.of(2020, 3, 14, 10, 0),
    rooms = Set(
      Room("r0"),
      Room("r1")
    ),
    candidateSkillMap = Map.empty
  )

  println(InterviewProducer.suggestInterviewCombinations(availability).mkString("\n"))
}
