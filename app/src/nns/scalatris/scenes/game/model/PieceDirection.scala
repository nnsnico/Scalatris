package nns.scalatris.scenes.game.model

import indigo.*

enum PieceDirection:
  case Neutral
  case Left
  case Right
  case Up
  case Down

object PieceDirection:

  enum ControlScheme:
    case Neutral
    case Turning(left: Key, right: Key)
    case Rotating(up: Key)
    case Falling(down: Key)

  val turningKeys: ControlScheme.Turning =
    ControlScheme.Turning(Key.KEY_H, Key.KEY_L)

  val rotatingKeys: ControlScheme.Rotating = ControlScheme.Rotating(Key.KEY_K)

  val fallingKeys: ControlScheme.Falling = ControlScheme.Falling(Key.KEY_J)

  extension (cs: ControlScheme)

    def toPieceDirection(keyboardEvent: KeyboardEvent): PieceDirection =
      (cs, keyboardEvent) match
        case (ControlScheme.Turning(key, _), KeyboardEvent.KeyDown(code))
            if code === key =>
          PieceDirection.Left
        case (ControlScheme.Turning(_, key), KeyboardEvent.KeyDown(code))
            if code === key =>
          PieceDirection.Right
        case (ControlScheme.Rotating(key), KeyboardEvent.KeyDown(code))
            if code === key =>
          PieceDirection.Up
        case (ControlScheme.Falling(key), KeyboardEvent.KeyDown(code))
            if code === key =>
          PieceDirection.Down
        case _ =>
          PieceDirection.Neutral
