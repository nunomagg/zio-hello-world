package basicExercises

object Looping extends zio.App {
  import zio.console._
  import zio._

  // R of type Console - this part is only needed because i added the putStr here,
  // which now needs to know what type of R it is so it can join with the R of effect
  def repeat[R <: Console,E,A](nTimes: Int)(effect: ZIO[R,E,A]): ZIO[R, E, A] = {
    if (nTimes<= 0) effect
    else putStr(s"[$nTimes] - ") *> effect *> repeat(nTimes-1)(effect)
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    repeat(100)(putStrLn("this will run a lot of times")) as ExitCode.success
}
