package de.detim.njustus.kafkaexample

import cats.effect.IO
import com.banno.kafka._
import com.banno.kafka.producer.ProducerApi
import fs2._
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serializer

case class Person(
                 name: String,
                 age: Int
                 )

class KafkaProducer(servers: BootstrapServers, topicName: String) {

  def produce(producerApi: ProducerApi[IO, String, String])(i:Int) = {
    println(s"sending $i,$i to $topicName")
    producerApi.sendAsync(new ProducerRecord(topicName, "key: "+i.toString, "value: "+i.toString))
  }

  def createProducer: Stream[IO, ProducerApi[IO, String, String]] =
    Stream.resource(ProducerApi.resource[IO, String, String](servers)).flatMap(p =>
      Stream.eval(IO.println(s"producer created")).map(_ => p)
    )
}
