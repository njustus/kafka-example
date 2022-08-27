package de.detim.njustus.kafkaexample

import cats.effect.{ExitCode, IO, IOApp}
import KafkaCirceSerializers._

import java.time.Instant

object PartitionedFiles extends IOApp {
  val messages:fs2.Stream[IO, dtos.Message] = fs2.Stream(
    dtos.LineEdit("file.txt", "test", dtos.Editor("nico"), 1, Instant.now()),
    dtos.LineEdit("file.txt", "test2", dtos.Editor("nico"), 1, Instant.now()),

    dtos.LineEdit("file2.txt", "test-2", dtos.Editor("paul"), 2, Instant.now()),
    dtos.LineEdit("file3.txt", "test-3", dtos.Editor("paul"), 2, Instant.now()),
  )

  override def run(args: List[String]): IO[ExitCode] = stream.compile.drain >> IO(ExitCode.Success)

  def stream = for {
    producer <- KafkaProducer.createProducer[dtos.Message](App.servers, "edit-partitions")
    _ <- messages.evalMap(m => producer.sendWithKey(m, m.fileName.hashCode.toString))
  } yield ()
}
