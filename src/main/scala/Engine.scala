object Engine {

  import InterviewADT._
  import InterviewOps._

  case class Combination(
      interview: Interview,
      availability: EventAvailability
  )

  private def produceNestedCombinations(
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

  private def produceCombinations(
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

  // TODO: Merge duplicate resources
  // TODO: Add unit tests
  private def simplify(availability: EventAvailability): EventAvailability = {
    val candidates   = mergeCandidates(availability.candidates)
    val interviewers = mergeInterviewers(availability.interviewers)

    availability.copy(
      candidates = candidates,
      interviewers = interviewers
    )
  }

  private def mergeInterviewers(interviewers: Set[Interviewer]): Set[Interviewer] = {
    interviewers
      .groupBy(_.name)
      .map {
        case (name, interviewers) =>
          Interviewer(name, interviewers.flatMap(_.skills))
      }
      .toSet
  }

  private def mergeCandidates(candidates: Set[Candidate]): Set[Candidate] = {
    candidates
      .groupBy(_.name)
      .map {
        case (name, candidates) => Candidate(name, candidates.flatMap(_.skills))
      }
      .toSet
  }

  def suggestInterviewCombinations(
      availability: EventAvailability): Set[Set[Interview]] = {
    val combinations       = produceNestedCombinations(Set.empty, availability)
    val maxParallelization = combinations.map(_.size).max

    combinations
      .filter(i => i.size == maxParallelization && i.nonEmpty)
  }

  private def getMatchingSkills(availability: EventAvailability)(
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
