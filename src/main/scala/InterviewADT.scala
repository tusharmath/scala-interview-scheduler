import java.time.LocalDateTime

import scala.concurrent.duration._

object InterviewADT {
  sealed trait Resource

  trait Experience
  object Experience {
    object High   extends Experience
    object Medium extends Experience
    object Low    extends Experience
  }

  // TODO: add skill priority
  case class Skill(name: String)

  // TODO: add candidate wait time
  // TODO: add candidate level
  case class Candidate(
      name: String,
      skills: Set[Skill] = Set.empty,
      company: Option[String] = None
  ) extends Resource

  // TODO: add interviewer level
  case class Interviewer(
      // Interviewer's name
      name: String,
      // Interviewer's Skills
      skills: Set[Skill] = Set.empty,
      // How preferred is this Interviewer
      // Typically we want to save certain interviewers for later
      preference: Experience = Experience.Medium
  ) extends Resource

  case class Room(name: String) extends Resource

  case class EventAvailability(
      // Interviewers for the interview
      interviewers: Set[Interviewer] = Set.empty,
      // Candidates for the interview
      candidates: Set[Candidate] = Set.empty,
      // Rooms available for the interview
      rooms: Set[Room] = Set.empty,
      // Skills that have been interviewed for
      candidateSkillMap: Map[Candidate, Set[Skill]] = Map.empty,
      // Time period for one interview
      interviewDuration: Duration = 1 hour,
      // Starting time for the interview
      interviewStartTime: LocalDateTime = LocalDateTime.now(),
      // Referrals
      referrals: Set[(Candidate, Interviewer)] = Set.empty
  )

  case class Combination(
      interview: Interview,
      availability: EventAvailability
  )

  case class Interview(
      interviewer: Interviewer,
      candidate: Candidate,
      skill: Skill,
      room: Room
  )
}
