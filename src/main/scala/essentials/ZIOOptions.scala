package essentials

import zio.{IO, ZIO}

object ZIOOptions {
  // Transformation of an option to zio
  // the error type of the resulting effect is Option[Nothing], which provides no information on why the value is not there.

  val zoption : IO[Option[Nothing], Int] = ZIO.fromOption(Some(2))

  // You can change the Option[Nothing] into a more specific error type using ZIO#mapError:
  val zoption2 : IO[String, Int] = zoption.mapError(_ => "There was no value!")

  //  You can also readily compose it with other operators while preserving the optional nature of the result (similar to an OptionT)

  type TeamId = String
  type UserId = String
  case class User(id: UserId, name: String, teamId: TeamId)
  case class Team(id: TeamId, name: String)
  val maybeId: IO[Option[Nothing], String] = ZIO.fromOption(Some("abc123"))

  def getUser(userId: String): IO[Throwable, Option[User]] = ???
  def getTeam(teamId: String): IO[Throwable, Team] = ???


  val result: IO[Throwable, Option[(User, Team)]] = (for {
    id   <- maybeId
    user <- getUser(id).some
    team <- getTeam(user.teamId).asSomeError
  } yield (user, team)).optional
}