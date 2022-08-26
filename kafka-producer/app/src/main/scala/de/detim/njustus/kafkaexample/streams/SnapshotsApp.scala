package de.detim.njustus.kafkaexample.streams

import cats.effect.{ExitCode, IO, IOApp}
import de.detim.njustus.kafkaexample.{App, KafkaConsumer, KafkaProducer, dtos}
import fs2._

object SnapshotsApp extends IOApp {
  import de.detim.njustus.kafkaexample.KafkaCirceSerializers._

  def applyPatch(state: dtos.FileContent, edit: dtos.LineEdit): dtos.FileContent = {
    if(edit.lineIdx >= state.content.size) {
      state.copy(content = state.content.appended(edit))
    } else {
      val (start, rest) = state.content.splitAt(edit.lineIdx)
      val newContent = start.appendedAll(rest.tail.prepended(edit))
      state.copy(content = newContent)
    }
  }

  private def snapshotStream: Stream[IO, dtos.Message] => Stream[IO, dtos.FileContent] = {
    val zero:Option[dtos.FileContent] = None
    _.scan(zero) {
      case (None, content: dtos.FileContent) =>
        println(s"received new content: $content")
        Some(content)
      case (Some(state), content: dtos.FileContent) =>
        println(s"ERROR: received content while having state $state , content: $content - returning initial state")
        Some(state)
      case (Some(state), edit: dtos.LineEdit) =>
        val newState = applyPatch(state, edit)
        println(s"received LineEdit: $edit. Applied it oldState: $state -- newState: $newState")
        Some(newState)
      case (state, x) =>
        println(s"ERROR: UNKNOWN MESSAGE. returning original state")
        state
    }.collect {
      case Some(x) => x
    }
  }

  override def run(args: List[String]): IO[ExitCode] = stream.compile.drain >> IO(ExitCode.Success)

  def stream = for {
    producer <- KafkaProducer.createProducer[dtos.Message](App.servers, App.snapshotTopic)
    _ <- KafkaConsumer.createConsumer[dtos.Message](App.servers, "scala-snapshot-creator", App.topic)
      .through(snapshotStream)
      .evalMap(producer.sendAsync)
  } yield ()
}
