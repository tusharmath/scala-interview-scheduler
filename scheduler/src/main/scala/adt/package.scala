import java.time.LocalDateTime

import scala.concurrent.duration._

package object adt {
  sealed abstract class EventResource(val kind: Int)
      extends Combination.Resource

  abstract class OrderPreference(value: Int)
  object OrderPreference {
    case object High    extends OrderPreference(2)
    case object Default extends OrderPreference(1)
    case object Low     extends OrderPreference(0)
  }

  // TODO: add skill priority
  case class Skill(name: String)

  // TODO: add candidate wait time
  // TODO: add candidate level
  case class Candidate(
      name: String,
      skills: Set[Skill] = Set.empty,
      company: Option[String] = None
  ) extends EventResource(0)

  // TODO: add interviewer level
  case class Interviewer(
      // Interviewer's name
      name: String,
      // Interviewer's Skills
      skills: Set[Skill] = Set.empty,
      // How preferred is this Interviewer
      // Typically we want to save certain interviewers for later
      preference: OrderPreference = OrderPreference.Default
  ) extends EventResource(1)

  case class Room(name: String) extends EventResource(2)

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

  case class Interview(
      interviewer: Interviewer,
      candidate: Candidate,
      skill: Skill,
      room: Room
  )
}
