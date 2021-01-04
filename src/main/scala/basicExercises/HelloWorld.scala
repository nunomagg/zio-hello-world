package basicExercises

import zio.{ExitCode, URIO}

object HelloWorld extends zio.App{
  import zio.console._

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    putStrLn("Hello, World!") as ExitCode.success
}