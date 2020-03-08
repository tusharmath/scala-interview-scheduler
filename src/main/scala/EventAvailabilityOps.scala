import InterviewADT._

object EventAvailabilityOps {
  implicit class EventAvailabilityOps(availability: EventAvailability) {
    def resourceCount: Int = {
      availability.rooms.size + availability.candidates.size + availability.interviewers.size
    }

    /**
      * Adding an interview to the event
      */
    def *(interview: Interview): EventAvailability = {

      val nCandidateSkillMap =
        availability.candidateSkillMap.get(interview.candidate) match {
          case Some(skillSet) =>
            val nSkillSet = skillSet + interview.skill
            availability.candidateSkillMap + (interview.candidate -> nSkillSet)
          case None => availability.candidateSkillMap
        }

      availability.copy(
        interviewers = availability.interviewers - interview.interviewer,
        candidates = availability.candidates - interview.candidate,
        rooms = availability.rooms - interview.room,
        candidateSkillMap = nCandidateSkillMap
      )
    }

    def has(resource: Resource): Boolean = {
      resource match {
        case c: Candidate   => availability.candidates.contains(c)
        case i: Interviewer => availability.interviewers.contains(i)
        case r: Room        => availability.rooms.contains(r)
      }
    }

    def +(resource: Resource): EventAvailability =
      resource match {
        case c: Candidate =>
          availability.copy(candidates = availability.candidates + c)
        case i: Interviewer =>
          availability.copy(interviewers = availability.interviewers + i)
        case r: Room => availability.copy(rooms = availability.rooms + r)
      }

    def -(resource: Resource): EventAvailability =
      resource match {
        case c: Candidate =>
          availability.copy(candidates = availability.candidates - c)
        case i: Interviewer =>
          availability.copy(interviewers = availability.interviewers - i)
        case r: Room => availability.copy(rooms = availability.rooms - r)
      }

    def interviewSuggestions: Set[Set[Interview]] = {
      InterviewSuggestions.suggestInterviews(availability)
    }
  }

}
