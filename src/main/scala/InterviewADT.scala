import java.time.LocalDateTime

import scala.concurrent.duration._

object InterviewADT {
  sealed trait Resource

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
  case class Interviewer(name: String, skills: Set[Skill] = Set.empty)
      extends Resource

  case class Room(name: String) extends Resource

  // TODO: add banned interviewer-candidate combinations
  case class EventAvailability(
      interviewers: Set[Interviewer] = Set.empty,
      candidates: Set[Candidate] = Set.empty,
      rooms: Set[Room] = Set.empty,
      candidateSkillMap: Map[Candidate, Set[Skill]] = Map.empty,
      interviewDuration: Duration = 1 hour,
      interviewStartTime: LocalDateTime = LocalDateTime.now()
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
