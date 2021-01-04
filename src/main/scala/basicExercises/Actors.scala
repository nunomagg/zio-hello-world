package basicExercises

import zio.console._
import zio.{ExitCode, Promise, Queue, Ref, Task, UIO, URIO, ZIO}

object Actors extends zio.App {

  sealed trait Command

  case object ReadTemperature extends Command

  case class AdjustTemperature(temperature: Double) extends Command

  type TemperatureActor = Command => Task[Double]

  def makeActor(initialTemperature: Double): UIO[TemperatureActor] = {
    type Bundle = (Command, Promise[Nothing, Double])

    // every actor has a queue
    // we are going to use a Promise to send the ref between actors

    for {
      ref <- Ref.make(initialTemperature)
      queue <- Queue.bounded[Bundle](1000)
      _ <- queue.take.flatMap {
        case (ReadTemperature, promise) => ref.get.flatMap(promise.succeed)
        case (AdjustTemperature(d), promise) => ref.updateAndGet( _ + d).flatMap(promise.succeed)
      }.forever.fork
    } yield (c: Command) => Promise.make[Nothing, Double].flatMap { p => queue.offer((c, p)) *> p.await }
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val temperatures = (0 to 1000).map(_.toDouble)

    val zioEffect = for {
      actor <- makeActor(0)
      _ <- ZIO.foreachPar(temperatures) { temp => actor(AdjustTemperature(temp)) }
      temp <- actor(ReadTemperature)
      _ <- putStrLn(s"Final temperature is $temp")
    } yield ExitCode.success

    zioEffect orElse ZIO.succeed(ExitCode.failure)
  }
}
