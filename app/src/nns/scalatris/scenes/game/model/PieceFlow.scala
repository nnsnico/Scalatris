package nns.scalatris.scenes.game.model

import cats.syntax.all.*

object PieceFlow:

  extension (flow: Seq[Piece])

    def next: Either[Throwable, (Piece, Seq[Piece])] = for {
      nextPiece <- flow
        .headOption
        .toRight(Exception("Unexpected Error: Pieces are empty in flow"))
      nextFlow <- flow.drop(1).asRight
    } yield (nextPiece, nextFlow)
