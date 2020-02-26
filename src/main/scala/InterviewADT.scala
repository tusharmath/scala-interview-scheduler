import java.time.LocalDateTime

import scala.concurrent.duration.Duration

object InterviewADT {
  case class Skill(name: String)
  case class Candidate(
      name: String,
      skills: Set[Skill] = Set.empty,
      company: Option[String] = None
  )
  case class Interviewer(name: String, skills: Set[Skill] = Set.empty)
  case class Room(name: String)
  case class EventAvailability(
      interviewers: Set[Interviewer],
      candidates: Set[Candidate],
      rooms: Set[Room],
      candidateSkillMap: Map[Candidate, Set[Skill]],
      interviewDuration: Duration,
      interviewStartTime: LocalDateTime
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
