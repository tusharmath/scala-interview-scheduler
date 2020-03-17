import InterviewADT._
import zio.test.Gen

object EventAvailabilityGen {
  def genName(prefix: String) = Gen.int(0, 6).map(name => s"r${name.toString}")
  def genRoom                 = genName("r").map(name => Room(name))
  def genSkill                = genName("s").map(name => Skill(name))
  def genSkillSet             = Gen.listOf(genSkill).map(skill => skill.toSet)
  def genCandidate =
    for {
      name     <- genName("c")
      skillSet <- genSkillSet
    } yield Candidate(name, skillSet)
  def genInterviewer = {
    for {
      name     <- genName("i")
      skillSet <- genSkillSet
    } yield Interviewer(name, skillSet)
  }
  def genResource =
    for {
      resourceGen <- Gen.elements(genCandidate, genInterviewer, genRoom)
      resource    <- resourceGen
    } yield resource
  def genInterviewerSet = Gen.listOf(genInterviewer).map(_.toSet)
  def genCandidateSet   = Gen.listOf(genCandidate).map(_.toSet)
  def genRoomSet        = Gen.listOf(genRoom).map(_.toSet)
  def genEventAvailability = {
    for {
      interviewers <- genInterviewerSet
      candidates   <- genCandidateSet
      rooms        <- genRoomSet
    } yield EventAvailability(interviewers, candidates, rooms)
  }
}
