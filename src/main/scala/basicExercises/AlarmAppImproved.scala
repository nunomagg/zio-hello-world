package basicExercises

import java.io.IOException
import java.util.concurrent.TimeUnit

import zio.clock.Clock
import zio.{ExitCode, URIO, ZIO}

object  AlarmAppImproved extends zio.App {

  import zio.console._
  import zio.duration._

  /**
   * This application will make use of fibers
   *
   * Fibers can be though of as lightweight green thread, They use Operating system threads but do not have a 1-to-1 mapping
   * A OS level thread can run hundreds or thousands of fibers
   *  - Fibers are non-blocking, everytime they wait on something they just suspend not actually consuming thread resources
   *    When the thing they are waiting on is available they resume
   *  - Fibers have global and shrinkable stack sizable unlike real threads (JVM or otherwise)
   *  - Fibers can be garbage collected if they are suspended and cannot resume
   *
   *
   *  All zio code runs inside a fiber, the main fiber. But we can create a new fiber
   *    the following example uses two fibers, one which sleeps (main fiber) and one to print out "." while the user is waiting
   */
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {

    val zioEffect = for {
      duration <- getAlarmDurationEffect()
      forever: ZIO[Clock with Console, Nothing, Nothing] = (putStr(".") *> ZIO.sleep(1.seconds)).forever // this returns zio effect that cannot fail (Nothing) and cannot succeed (nothing)
      fiber <- forever.fork // this creates a new fiber which will sleep
      _ <- ZIO.sleep(duration) // looks like its blocking, but it's actually async
      _ <- fiber.interrupt //ends the fiber's life!!!
      _ <- putStrLn(s"\nWoke up after ${duration.toMillis} millis")
    } yield ExitCode.success

    zioEffect as ExitCode.success orElse ZIO.succeed(ExitCode.success)
  }


  private def getAlarmDurationEffect(): ZIO[Console, IOException, Duration] = {
    lazy val fallback = putStrLn("Failed!!") *> getAlarmDurationEffect()

    for {
      _ <- putStrLn("Please enter the amount of seconds to sleep:")
      input <- getStrLn
      duration <- ZIO.fromEither(parseDuration(input)) orElse fallback
    } yield duration
  }

  private def parseDuration(input: String): Either[NumberFormatException, Duration] = {
    toDouble(input).map(seconds => Duration((seconds * 1000).toLong, TimeUnit.MILLISECONDS))
  }

  private def toDouble(input: String): Either[NumberFormatException, Double] = {
    try {
      Right(input.toDouble)
    } catch {
      case e: NumberFormatException => Left(e)
    }
  }
}
