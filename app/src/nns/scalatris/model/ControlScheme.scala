package nns.scalatris.model

import indigo.*

enum ControlScheme:
  case Turning(left: Key, right: Key)
  case Falling(down: Key)

object ControlScheme:
  val turningKeys: Turning = Turning(Key.LEFT_ARROW, Key.RIGHT_ARROW)
  val fallingKeys: Falling = Falling(Key.DOWN_ARROW)

  extension (cs: ControlScheme)

    def controlPiece(keyboardEvent: KeyboardEvent, piece: Piece): Piece =
      (cs, keyboardEvent) match
        case (ControlScheme.Turning(left, _), KeyboardEvent.KeyDown(code))
            if code === left =>
          piece.changeDirection(PieceDirection.Left)
        case (ControlScheme.Turning(_, right), KeyboardEvent.KeyDown(code))
            if code === right =>
          piece.changeDirection(PieceDirection.Right)
        case (ControlScheme.Falling(down), KeyboardEvent.KeyDown(code))
            if code === down =>
          piece.changeDirection(PieceDirection.Down)
        case _ =>
          piece
