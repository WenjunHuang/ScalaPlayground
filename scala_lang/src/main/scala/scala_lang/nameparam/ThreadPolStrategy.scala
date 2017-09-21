package scala_lang.nameparam

import java.util.concurrent.{Callable, Executors}

object ThreadPoolStrategy extends ThreadStrategy {
  val pool = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors())

  override def execute[A](func: () ⇒ A) = {
    val future = pool.submit(new Callable[A] {
      override def call(): A = {
        Console.println("Executing function on thread: " +
          Thread.currentThread().getName)
        func()
      }
    })
    () ⇒ future.get()
  }
}
