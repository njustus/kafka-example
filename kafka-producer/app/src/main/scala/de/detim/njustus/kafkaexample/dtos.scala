package de.detim.njustus.kafkaexample

import java.time.Instant

object dtos {
  case class Point(x: Int, y:Int)
  case class Editor(username: String)

  type Lines = IndexedSeq[LineEdit]

  sealed trait Message
  case class LineEdit(fileName: String,
                      content: String,
                      user: Editor,
                      lineIdx: Int,
                      timestamp: Instant
                     ) extends Message

  case class FileContent(fileName: String,
                         content: Lines,
                         user: Editor,
                         timestamp: Instant)
    extends Message
}
