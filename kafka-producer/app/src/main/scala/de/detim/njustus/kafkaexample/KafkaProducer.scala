package de.detim.njustus.kafkaexample

import cats.effect.IO
import com.banno.kafka._
import com.banno.kafka.producer.ProducerApi
import fs2._
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}
import KafkaCirceSerializers._
import org.apache.kafka.common.serialization.Serializer

case class Person(
                 name: String,
                 age: Int
                 )

class KafkaProducer[A <: dtos.KeyeableMessage : Serializer](topicName: String, producer: ProducerApi[IO, String, A]) {
  def sendAsync(msg:A): IO[RecordMetadata] = sendWithKey(msg, msg.key.toString)

  def sendWithKey(msg: A, key:String): IO[RecordMetadata] = {
    IO.println(s"sending $msg downstream to topic $topicName") >>
    producer.sendAsync(
      new ProducerRecord(topicName, key, msg)
    )
  }
}

object KafkaProducer {

  def createProducer[A <: dtos.KeyeableMessage : Serializer](servers: BootstrapServers, topicName: String): Stream[IO, KafkaProducer[A]] =
    Stream.resource(ProducerApi.resource[IO, String, A](servers)).flatMap(p =>
      Stream.eval(IO.println(s"producer created"))
        .map(_ => new KafkaProducer[A](topicName, p))
    )
}
