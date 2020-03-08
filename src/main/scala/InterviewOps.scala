import InterviewADT._

object InterviewOps {
  implicit class EventAvailabilityOps(availability: EventAvailability) {

    /**
      * Add room to the event
      */
    def +(room: Room): EventAvailability = availability.copy(
      rooms = availability.rooms + room
    )

    /**
      * Add interviewer to the event
      */
    def +(interviewer: Interviewer): EventAvailability = availability.copy(
      interviewers = availability.interviewers + interviewer
    )

    /**
      * Add candidate to the event
      */
    def +(candidate: Candidate): EventAvailability = availability.copy(
      candidates = availability.candidates + candidate,
      candidateSkillMap = availability.candidateSkillMap + (candidate -> candidate.skills)
    )

    /**
      * Remove an interviewer from the event
      */
    def -(interviewer: Interviewer): EventAvailability = availability.copy(
      interviewers = availability.interviewers - interviewer
    )

    /**
      * Remove a candidate from the event
      */
    def -(candidate: Candidate): EventAvailability = availability.copy(
      candidates = availability.candidates - candidate,
    )

    /**
      * Remove room from the event
      */
    def -(room: Room): EventAvailability = availability.copy(
      rooms = availability.rooms - room
    )

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

    def +(resource: Resource): EventAvailability = ???
    def interviewSuggestions: Set[Set[Interview]] = {
      InterviewSuggestions.suggestInterviews(availability)
    }
  }

}
