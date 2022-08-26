package de.detim.njustus.kafkaexample

import cats.effect.IO
import com.banno.kafka._
import com.banno.kafka.consumer.ConsumerApi
import fs2._
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import KafkaCirceSerializers._
import org.apache.kafka.common.serialization.{Deserializer}
object KafkaConsumer {

  import scala.concurrent.duration._

  def createConsumer[A:Deserializer](servers: BootstrapServers, clientId: String, topicName: String): Stream[IO, A] =
    Stream.resource(ConsumerApi.resource[IO, String, A](
      servers,
      ClientId(clientId)
    )).flatMap { c =>
      Stream.eval(c.assign(topicName, Map.empty[TopicPartition, Long])) >>
      c.recordStream(5.second)
    }.map(x => x.value())
}
