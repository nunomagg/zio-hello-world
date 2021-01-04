package essentials

object ZIOTypeAliases {
  /**
   * https://zio.dev/docs/overview/overview_index
   *
   * R - Environment Type. The effect requires an environment of type R.
   *      If this type parameter is Any, it means the effect has no requirements, because you can run the effect with any value (for example, the unit value ()).
   * E - Failure Type. The effect may fail with a value of type E. Some applications will use Throwable.
   *      If this type parameter is Nothing, it means the effect cannot fail, because there are no values of type Nothing.
   * A - Success Type. The effect may succeed with a value of type A. If this type parameter is Unit, it means the effect produces no useful information,
   *          while if it is Nothing, it means the effect runs forever (or until failure).
   *
   * A value of type ZIO[R, E, A] is like an effectful version of the following function type:
   *          R => Either[E, A]
   *
   *
   */

  /**
   * UIO[A] = ZIO[Any, Nothing, A] - represents an effect that has no requirements, and cannot fail, but can succeed with an A.
   *     - can be useful for describing infallible effects, including those resulting from handling all errors.
   * URIO[R, A] = ZIO[R, Nothing, A] -  represents an effect that requires an R, and cannot fail, but can succeed with an A.
   * Task[A] = ZIO[Any, Throwable, A] - represents an effect that has no requirements, and may fail with a Throwable value, or succeed with an A.
   *    - Corresponds most closely to the Future data type from Scala
   * RIO[R, A]  ZIO[R, Throwable, A] - represents an effect that requires an R, and may fail with a Throwable value, or succeed with an A.
   * IO[E, A] — ZIO[Any, E, A] - represents an effect that has no requirements, and may fail with an E, or succeed with an A.
   */
}