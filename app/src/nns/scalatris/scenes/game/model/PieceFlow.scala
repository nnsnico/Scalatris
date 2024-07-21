package nns.scalatris.scenes.game.model

import cats.syntax.all.*
import mouse.all.*
import nns.scalatris.assets.BlockMaterial

import scala.util.Random

object PieceFlow:

  extension (flow: Seq[Piece])

    def next(
        blockMaterials: Seq[BlockMaterial],
    ): Either[Throwable, (Piece, Seq[Piece])] = for {
      nextPiece <-
        flow
          .headOption
          .toRight(Exception("Unexpected Error: Pieces are empty in flow"))
      candidateNextFlow <- flow.drop(1).asRight
      nextFlow <-
        (!candidateNextFlow.isEmpty).fold(
          t = candidateNextFlow.asRight,
          f = Piece.createPieceFlow(blockMaterials).map(Random.shuffle(_)),
        )
    } yield (nextPiece, nextFlow)
