package basicExercises

object PrintSequence extends zio.App {

  import zio.console._
  import zio._

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = { // ZIO[zEnv, Nothing, ExitCode]
    // THIS WONT RUN because the effects are lazy, so this would only run the last effect, which is the success - on contrary to Future
//    putStrLn("Hello")
//    putStrLn("World")
//    putStrLn("!")
//    IO.succeed(ExitCode.success)

    val printResult = for {
      _ <- putStrLn("Hello")
      _ <- putStrLn("World")
      _ <- putStrLn("!")
    } yield ()

    val resultExitCode = printResult.exitCode

    resultExitCode
  }
}