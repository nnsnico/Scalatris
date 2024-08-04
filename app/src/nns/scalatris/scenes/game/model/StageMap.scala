package nns.scalatris.scenes.game.model

import indigo.*
import cats.syntax.all.*

object StageMap:

  extension (stage: Set[Piece])

    def putOrStay(putPiece: Piece): Option[Set[Piece]] = {
      val stageVertex = stage.flatMap(_.currentPosition)
      val putPieceVertex = putPiece.currentPosition.toSet

      if ((stageVertex & putPieceVertex).size >= 1) {
        none
      } else {
        (stage + putPiece).some
      }
    }
