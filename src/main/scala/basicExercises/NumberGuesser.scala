package basicExercises

import zio.{ExitCode, URIO, ZIO}

object NumberGuesser extends zio.App {

  import zio.random._
  import zio.console._

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val zioEffect = for {
    _ <- putStrLn("Guess a number from 0 to 3: ")
    guess <- getStrLn
//    randomValue <- generateRandomValue(3)
    randomValue <- generateRandomScala(3)
    _ <- putStrLn(s"this was your answer: $guess")
    _ <- putStrLn(s"This was the randomValue: $randomValue")
    _ <- analiseAnswer(guess, randomValue)
    } yield ExitCode.success

    zioEffect orElse ZIO.succeed(ExitCode.success)
  }

  private def generateRandomValue(seed: Int) = nextIntBounded(seed) // This is a zio Random, which returns an effect of a Random number

  private def generateRandomScala(seed: Int) = {
    import scala.util.Random._

    // This creates an effect with a scala random, it's expecting that random does not throw any type of errors
    ZIO.effectTotal(scala.util.Random.nextInt(seed))

    // if it is possible to return errors use to define the type of exception it can throw
    // the result from this will be a ZIO[IOException, E1, Int]
    //    ZIO.effect(scala.util.Random.nextInt(3)).refineToOrDie[IOException]
  }

  private def analiseAnswer(guess: String, random: Int) = {
    if (guess.trim == random.toString)
      putStrLn("Your answer was correct")
    else
      putStrLn("Your answer was incorrect")
  }
}
