package basicExercises

import zio.duration.durationInt

object ComputePi extends zio.App {
  import zio.console._
  import zio.random._
  import zio._

  final case class PiState(inside: Long, total: Long)

  private def estimatePi(inside: Long, total: Long) : Double = {
    (inside.toDouble / total.toDouble) * 4.0
  }

  private def insideCircle(x: Double, y: Double): Boolean = Math.sqrt(x*y + y *y)<= 1

  private val randomPoint: URIO[Random, (Double, Double)] = nextDouble zip nextDouble

  private def updateOnce(ref: Ref[PiState]): ZIO[Random with Console, NoSuchElementException, Unit] =
    for {
      (x,y) <- randomPoint
      inside = if (insideCircle(x, y)) 1 else 0
      _ <- ref.update(state => PiState(state.inside + inside, state.total +  1))
    } yield ()

  private def printEstimate(ref: Ref[PiState]): ZIO[Console, Nothing, Unit] =
    for {
      state <- ref.get
      _ <- putStrLn(s"PI estimate is: ${estimatePi(state.inside, state.total)}")
    } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
   val zioEffect =  for {
      ref <- Ref.make(PiState(0L,0L))
      worker = updateOnce(ref).forever
      workers = List.fill(4)(worker)
      fiber1 <- ZIO.forkAll(workers)
      fiber2 <- (printEstimate(ref) *> ZIO.sleep(1.second)).forever.fork
      _ <- putStrLn("Click any key to terminate")
      _ <- getStrLn *> putStrLn("terminating...") *>(fiber1 zip fiber2).interrupt
    } yield ExitCode.success

    zioEffect orElse( ZIO.succeed(ExitCode.success))
  }
}
