object InterviewSuggestions {
  import InterviewADT._
  import Prelude._

  def toInterview(set: Set[EventResource]): Option[Interview] =
    for {
      interviewer <- set.findByType[Interviewer]
      candidate   <- set.findByType[Candidate]
      room        <- set.findByType[Room]
      skill       <- interviewer.skills.intersect(candidate.skills).headOption
    } yield Interview(interviewer, candidate, skill, room)

  def suggestInterviews(
      availability: EventAvailability
  ): Set[Set[Interview]] = {
    val resources = getResources(availability)

    CombinationBuilder
      .combination(resources)
      .map(_.flatMap(toInterview(_).toSet))
      .filter(_.nonEmpty)
  }

  private def getResources(
      availability: EventAvailability): Set[EventResource] = {
    availability.candidates ++ availability.interviewers ++ availability.rooms
  }
}
