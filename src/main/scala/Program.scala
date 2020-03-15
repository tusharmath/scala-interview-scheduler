import java.io.IOException

import InterviewADT.{Candidate, Interviewer, Skill}
import zio.console.{Console, getStrLn, putStr, putStrLn}
import zio.{UIO, ZIO}

object Program {

  def getSkillSet: ZIO[Console, IOException, Set[Skill]] =
    for {
      input <- ask("Enter Unique Skills: ")
    } yield {
      tokenfy(input)
        .map(Skill)
        .toSet
    }

  def ask(question: String): ZIO[Console, IOException, String] =
    for {
      _     <- putStr(question)
      input <- getStrLn
    } yield input

  def say(input: Any*): ZIO[Console, Nothing, Unit] =
    for {
      _ <- putStrLn(input.map(_.toString).mkString(" "))
    } yield ()

  def tokenfy(input: String): Array[String] = {
    input
      .split(" ")
      .filter(_.nonEmpty)
  }

  def selectSkill(
      inputSkillSet: Set[Skill],
      selectedSkillSet: Set[Skill] = Set.empty
  ): ZIO[Console, IOException, Set[Skill]] = inputSkillSet.headOption match {
    case None => UIO(selectedSkillSet)
    case Some(skill) =>
      for {
        input <- getBooleanLn(s"Select '${skill.name}' ")
        nSelectedSkillSet = if (input) selectedSkillSet + skill
        else selectedSkillSet
        out <- selectSkill(inputSkillSet - skill, nSelectedSkillSet)
      } yield out
  }

  def addInterviewer(
      skillSet: Set[Skill]
  ): ZIO[Console, IOException, Interviewer] = {
    for {
      name           <- ask("Enter Interviewer Name:")
      selectedSkills <- selectSkill(skillSet)
    } yield Interviewer(name, selectedSkills)
  }

  def printSkills(skillSeq: Set[Skill]) =
    for {
      _ <- say("Skills:")
      _ <- say(
        skillSeq.zipWithIndex
          .map {
            case (skill, index) => {
              val indexString = s"${index + 1}.    ".take(3)
              s"${indexString} ${skill.name}"
            }
          }
          .mkString("\n")
      )
    } yield ()

  def getBooleanLn(message: String): ZIO[Console, IOException, Boolean] =
    for {
      answer <- ask(message + "(y/n): ")
    } yield answer.toLowerCase == "y"

  def addInterviewers(
      skillSet: Set[Skill]
  ): ZIO[Console, IOException, Set[Interviewer]] =
    for {
      interviewer <- addInterviewer(skillSet)
      canContinue <- getBooleanLn("Add more interviewers ")
      interviewers <- if (canContinue)
        addInterviewers(skillSet).map(_ + interviewer)
      else UIO(Set(interviewer))
    } yield interviewers

  def addCandidates(
      skillSet: Set[Skill]
  ): ZIO[Console, IOException, Set[Candidate]] =
    for {
      interviewer <- addCandidate(skillSet)
      canContinue <- getBooleanLn("Add more candidates ")
      interviewers <- if (canContinue)
        addCandidates(skillSet).map(_ + interviewer)
      else UIO(Set(interviewer))
    } yield interviewers

  def addCandidate(
      skillSet: Set[Skill]
  ): ZIO[Console, IOException, Candidate] = {
    for {
      name           <- ask("Enter Candidate Name:")
      selectedSkills <- selectSkill(skillSet)
    } yield Candidate(name, selectedSkills)
  }

  def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val program = for {
      _            <- say("\nWelcome!\n")
      skills       <- getSkillSet
      _            <- printSkills(skills)
      interviewers <- addInterviewers(skills)
      candidates   <- addCandidate(skills)
      _            <- say(interviewers.toString())
    } yield 0

    program.catchAll(err =>
      for {
        _ <- say(err.getMessage)
      } yield 1)
  }
}
