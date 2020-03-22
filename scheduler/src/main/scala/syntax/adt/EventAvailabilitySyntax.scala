package syntax.adt

import adt._

trait EventAvailabilitySyntax {
  implicit class EventAvailabilityOps(availability: EventAvailability) {

    def +(resource: EventResource): EventAvailability =
      resource match {
        case c: Candidate =>
          availability.copy(candidates = availability.candidates + c)
        case i: Interviewer =>
          availability.copy(interviewers = availability.interviewers + i)
        case r: Room => availability.copy(rooms = availability.rooms + r)
      }

    def -(resource: EventResource): EventAvailability =
      resource match {
        case c: Candidate =>
          availability.copy(candidates = availability.candidates - c)
        case i: Interviewer =>
          availability.copy(interviewers = availability.interviewers - i)
        case r: Room => availability.copy(rooms = availability.rooms - r)
      }

  }
}
