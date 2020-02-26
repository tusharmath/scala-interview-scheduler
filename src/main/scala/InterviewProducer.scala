object InterviewProducer {

  import InterviewADT._
  import InterviewOps._

  def produceNestedCombinations(
      decidedInterviews: Set[Interview],
      availability: EventAvailability
  ): Set[Set[Interview]] = {
    val proposedCombinations = produceCombinations(availability)
    if (proposedCombinations.isEmpty) Set(decidedInterviews)
    else
      for {
        combination <- proposedCombinations
        interviewSet <- produceNestedCombinations(
          decidedInterviews + combination.interview,
          combination.availability
        )
      } yield interviewSet

  }

  def produceCombinations(
      availability: EventAvailability
  ): Set[Combination] = {
    for {
      interviewer <- availability.interviewers
      candidate   <- availability.candidates
      room        <- availability.rooms
      skill       <- getMatchingSkills(availability)(candidate, interviewer, room)
    } yield {
      val interview     = Interview(interviewer, candidate, skill, room)
      val nAvailability = availability * interview
      Combination(interview, nAvailability)
    }
  }

  def suggestInterviewCombinations(
      availability: EventAvailability
  ): Set[Set[Interview]] = {
    val combinations = produceNestedCombinations(Set.empty, availability)
    val maxParallelization = combinations.map(_.size).max

    combinations.filter(_.size == maxParallelization)
  }

  def getMatchingSkills(availability: EventAvailability)(
      candidate: Candidate,
      interviewer: Interviewer,
      room: Room
  ): Set[Skill] = {
    val candidateAvailable   = availability.candidates contains candidate
    val interviewerAvailable = availability.interviewers contains interviewer
    val roomAvailable        = availability.rooms contains room

    if (candidateAvailable && interviewerAvailable && roomAvailable)
      availability.candidateSkillMap.get(candidate) match {
        case Some(skills) => skills.diff(interviewer.skills)
        case None         => interviewer.skills.intersect(candidate.skills)
      } else Set.empty
  }

  def s(nest: Int): String = {
    "".padTo(nest, ' ')
  }
}
