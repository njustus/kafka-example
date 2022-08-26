package de.detim.njustus.kafkaexample

import cats.effect.IO
import com.banno.kafka._
import com.banno.kafka.consumer.ConsumerApi
import fs2._
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition

class KafkaConsumer(servers: BootstrapServers) {

  import scala.concurrent.duration._

  def createConsumer(clientId: String, topicName: String) =
    Stream.resource(ConsumerApi.resource[IO, String, String](
      servers,
      ClientId(clientId)
    )).flatMap { c =>
      Stream.eval(c.assign(topicName, Map.empty[TopicPartition, Long])) >>
      c.recordStream(5.second)
    }.map(x => x.value())
}
