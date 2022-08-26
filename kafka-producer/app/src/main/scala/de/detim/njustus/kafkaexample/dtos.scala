package de.detim.njustus.kafkaexample

import java.time.Instant

object dtos {
  case class Point(x: Int, y:Int)
  case class Editor(username: String)

  type Lines = IndexedSeq[LineEdit]

  trait KeyeableMessage {
    type A
    def key: A
  }

  sealed trait Message extends KeyeableMessage {
    type A = String
    def fileName: String
    def key: A = fileName
  }

  case class LineEdit(override val fileName: String,
                      content: String,
                      user: Editor,
                      lineIdx: Int,
                      timestamp: Instant
                     ) extends Message

  case class FileContent(override val fileName: String,
                         content: Lines,
                         user: Editor,
                         timestamp: Instant)
    extends Message
}
