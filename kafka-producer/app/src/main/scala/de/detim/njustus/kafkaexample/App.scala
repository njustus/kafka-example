/*
 * This Scala source file was generated by the Gradle 'init' task.
 */
package de.detim.njustus.kafkaexample

import cats.effect._
import com.banno.kafka.BootstrapServers

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = for {
    _ <- IO.println("starting producer...")
    producerClass = new KafkaProducer(BootstrapServers("localhost:29092"), "file-updates")
    consumerClass = new KafkaConsumer(BootstrapServers("localhost:29092"))
    _ <- producerClass.createProducer.evalMap(producerClass.produce(_)(5)).compile.drain
    _ <- consumerClass.createConsumer("test-client", "file-updates")
      .evalMap(x => IO.print(s"consumer received: $x"))
      .take(1)
      .compile.drain
  } yield ExitCode.Success
}
