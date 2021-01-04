package basicExercises

import zio.console.{Console, putStrLn}
import zio.{ExitCode, URIO, ZIO}
import zio.stm.{STM, TRef, USTM}

/** STM: Software transactional memory
 * the transaction will give atomic, consistent and isolated  (ACI)
 *
 * an ordinary zio Ref gives the guarantee of ACI in a single zio ref
 * where STM allows for  multiple operation across multiple refs
 *
 * Problem:
 *    A round table is set with one fork on the side of each philosophers, all the philosophers need to eat their meal
 *    for that they need to use two forks at same time.
 *    This is a concurrency problem ofc
 * */
object StmDinningPhilosophers extends zio.App {

  final case class Fork(number: Int)

  // T ref is a Transactional Reference, ie: A transactional var
  final case class Placement(left: TRef[Option[Fork]], right: TRef[Option[Fork]])

  final case class RoundTable(seats: Vector[Placement])

  /** While a Ref give Atomic guarantee only on the content
   * T ref has composable transactionality, we can modify many TRefs in the context of a single transaction
   * that Transaction type is STM
   */

  //Tries to take the forks
  private def takeForksV1(left: TRef[Option[Fork]], right: TRef[Option[Fork]]): STM[Nothing, (Fork, Fork)] = {

    for {
      leftOption <- left.get
      leftFork <- leftOption match {
        case None => STM.retry // if there is no fork, suspends the transaction until someone modifies this fork, once it exists then retry this whole function (takeforks)
        case Some(fork) => STM.succeed(fork)
      }
      //      leftFork <- left.get.collect{case Some(fork) => fork} // This does the same as the previous two lines
      rightFork <- right.get.collect { case Some(fork) => fork }
    } yield (leftFork, rightFork)
  }

  //simplified version of takeForks
  private def takeForksV2(left: TRef[Option[Fork]], right: TRef[Option[Fork]]): STM[Nothing, (Fork, Fork)] = {
    left.get.collect { case Some(fork) => fork } zip right.get.collect { case Some(fork) => fork }
  }

  //used when the philosopher is done eating
  private def putForks(left: TRef[Option[Fork]], right: TRef[Option[Fork]])(tuple: (Fork, Fork)): STM[Nothing, Unit] = {
    val (leftFork, rightFork) = tuple

    for {
      _ <- left.set(Some(leftFork))
      _ <- right.set(Some(rightFork))
    } yield ()
  }

  def setupTable(size: Int): URIO[Any, RoundTable] = {
    def makeFork(i: Int): USTM[TRef[Option[Fork]]] = TRef.make(Some(Fork(i)))

    val stm = for {
      allForks0 <- STM.foreach(0 to size)(i => makeFork(i))
      allForks = allForks0 ++ List(allForks0.head)
      placements = (allForks zip allForks.drop(1)).map { case (l, r) => Placement(l, r) }
    } yield RoundTable(placements.toVector)

    stm.commit
  }

  def eat(philosopher: Int, roundTable: RoundTable): URIO[Console, Unit] = {
    val placement = roundTable.seats(philosopher)

    val left = placement.left
    val right = placement.right

    for {
      forks <- takeForksV2(left, right).commit
      _ <- putStrLn(s"Philosopher $philosopher is eating...")
      _ <- putForks(left, right)(forks).commit
      _ <- putStrLn(s"Philosopher $philosopher is done eating")
    } yield ()
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val nrPhilosophers = 10

    def makeEaters(table: RoundTable): Seq[URIO[Console, Unit]] = {
      (0 to nrPhilosophers).map{index => eat(index, table)}
    }

    for {
      table <- setupTable(nrPhilosophers)
      fiber <-  ZIO.forkAll(makeEaters(table))
      _ <- fiber.join // only returns when all the forks have ended
      _ <- putStrLn("Everyone has eaten!")
    } yield ExitCode.success
  }
}
