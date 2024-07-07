package nns.scalatris.scenes.title.model

import indigo.*
import indigo.scenes.*

enum CursorDirection:
  case Neutral
  case Enter
  case Up
  case Down

object CursorDirection:

  enum ControlScheme:
    case Neutral
    case Enter(enter: Key)
    case MoveDown(down: Key)
    case MoveUp(up: Key)

  val enterKeys: ControlScheme.Enter       = ControlScheme.Enter(Key.ENTER)
  val moveDownKeys: ControlScheme.MoveDown = ControlScheme.MoveDown(Key.KEY_J)
  val moveUpKeys: ControlScheme.MoveUp     = ControlScheme.MoveUp(Key.KEY_K)

  extension (scheme: ControlScheme)

    def toCursorDirection(event: KeyboardEvent): CursorDirection =
      (scheme, event) match
        case (ControlScheme.MoveDown(key), KeyboardEvent.KeyDown(code))
            if code === key =>
          CursorDirection.Down
        case (ControlScheme.MoveUp(key), KeyboardEvent.KeyDown(code))
            if code === key =>
          CursorDirection.Up
        case (ControlScheme.Enter(key), KeyboardEvent.KeyDown(code))
            if code === key =>
          CursorDirection.Enter
        case _ =>
          CursorDirection.Neutral
