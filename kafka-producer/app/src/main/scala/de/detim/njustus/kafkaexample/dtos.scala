package de.detim.njustus.kafkaexample

object dtos {
  case class Point(x: Int, y:Int)

  type Lines = IndexedSeq[String]

  sealed trait Message
  case class LineEdit(fileName: String,
                      content: String,
                      start: Point
                     ) extends Message

  case class FileContent(fileName: String,
                         content:Lines)
    extends Message
}
