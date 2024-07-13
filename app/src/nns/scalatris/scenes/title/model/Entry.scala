package nns.scalatris.scenes.title.model

import nns.scalatris.scenes.title.model.Index

enum SelectStatus:
  case Selecting
  case NotSelected

final case class Entry(
    index: Index,
    text: String,
    status: SelectStatus,
)

extension (s: SelectStatus)

  def toText: String = s match {
    case SelectStatus.Selecting   => ">"
    case SelectStatus.NotSelected => " "
  }
