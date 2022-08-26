package de.detim.njustus.kafkaexample

import io.circe.generic.auto._
import io.circe.generic.semiauto._
import org.apache.kafka.common.serialization.{Deserializer, Serializer}

import java.nio.charset.StandardCharsets

object KafkaCirceSerializers {
  import dtos._

  private val encoder = deriveEncoder[Message]
  private val decoder = deriveDecoder[Message]

  implicit val serializer: Serializer[Message] = new Serializer[Message] {
    override def serialize(topic: String, dto: Message): Array[Byte] =
      encoder.apply(dto).spaces2.getBytes(StandardCharsets.UTF_8)
  }

  implicit val deserializer: Deserializer[Message] = new Deserializer[Message] {
    override def deserialize(s: String, bytes: Array[Byte]): Message = {
      val str = new String(bytes, StandardCharsets.UTF_8)
      val json = io.circe.parser.parse(str)
        .getOrElse(throw new IllegalArgumentException(s"Don't know how to parse: $str"))

      decoder.decodeJson(json).getOrElse(
        throw new IllegalArgumentException(s"Can not deserialize $str to 'Message'")
      )
    }
  }
}
