import java.time.LocalDateTime

import scala.concurrent.duration._
object Main extends App {

  import InterviewADT._
  import EventAvailabilityOps._
  import InterviewSuggestionsOps._

  private val s0 = Skill("s0")
  private val s1 = Skill("s1")

  private val interviewers = Set(
    Interviewer("i0", Set(s0)),
    Interviewer("i1", Set(s0, s1)),
  )
  private val candidates = Set(
    Candidate("c0", Set(s0)),
    Candidate("c1", Set(s1)),
  )
  private val rooms = Set(
    Room("r0"),
    Room("r1")
  )
  private val availability = EventAvailability(
    interviewers = interviewers,
    candidates = candidates,
    interviewDuration = 45 minutes,
    interviewStartTime = LocalDateTime.of(2020, 3, 14, 10, 0),
    rooms = rooms
  )

  println(availability.interviewSuggestions.show)
}
