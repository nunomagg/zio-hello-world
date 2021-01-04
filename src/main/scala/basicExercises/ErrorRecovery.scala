package basicExercises

object ErrorRecovery extends zio.App {
  import zio.console._
  import zio._

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val failed = putStrLn("This is about to fail") *>
      ZIO.fail("Oh no! A failure") *> // Failure type
      putStrLn("this will NEVER be printed")


    /** All the responses are equivalent **/
    //This is expecting an effect so we need to create a succeed effect with the Failed status code
    // zio as is he same as .map(_ => asValue)
    val response1 = (failed as ExitCode.success).orElse(ZIO.succeed(ExitCode.failure))

    val response2 = failed.fold(_=> ExitCode.success, _ => ExitCode.failure)

    // zio allows for fiber dump which is like a thread dump but for fibers
    val response3 = (failed as ExitCode.success).catchAllCause {cause: Cause[String] => // this is string because the failure type is string, see above
      putStrLn(s"${cause.prettyPrint}") as ExitCode.success
    }

    response3
  }
}