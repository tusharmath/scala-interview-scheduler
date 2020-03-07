import java.time.LocalDateTime

import scala.concurrent.duration._
import scala.language.postfixOps

object InterviewADT {
  // TODO: add skill priority
  case class Skill(name: String)

  // TODO: add candidate wait time
  // TODO: add candidate level
  case class Candidate(
      name: String,
      skills: Set[Skill] = Set.empty,
      company: Option[String] = None
  )

  // TODO: add interviewer level
  case class Interviewer(name: String, skills: Set[Skill] = Set.empty)

  case class Room(name: String)

  // TODO: add banned interviewer-candidate combinations
  case class EventAvailability(
      interviewers: Set[Interviewer],
      candidates: Set[Candidate],
      rooms: Set[Room],
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
  ) {
    override def toString: String = {
      s"[${interviewer.name} ${candidate.name} ${room.name}]"
    }
  }
}
