import InterviewADT.Interview

object InterviewSuggestionsOps {
  implicit class InterviewSuggestionsOps(suggestions: Set[Set[Interview]]) {
    def show: String =
      suggestions
        .map(suggestion =>
          suggestion.map(interview =>
            s"[${interview.interviewer.name} ${interview.candidate.name} ${interview.room.name}]"))
        .mkString("\n")
  }
}
