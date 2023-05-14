package nns.scalatris.scenes.game

import indigo.*
import indigo.logger._
import nns.scalatris.assets._
import nns.scalatris.model.{GlobalModel, Piece}
import nns.scalatris.{GridSquareSize, ViewConfig}

import scala.util.Random

object GameView:

  def update(
      viewConfig: ViewConfig,
      model: GameModel,
      stage: Group,
  ): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment
        .empty
        .addLayer(
          Layer(
            BindingKey("ui"),
            stage :: model
              .piece
              .map(p => drawPiece(piece = p))
              .orNull,
          ),
        ),
    )

  private def drawPiece(piece: Piece): Batch[Graphic[_]] = Batch.fromSeq(
    piece.localPos.map { pos =>
      piece
        .material
        .bitmap
        .moveTo(
          x = ((piece.blockSize * pos.x) + piece.position.x).toInt,
          y = ((piece.blockSize * pos.y) + piece.position.y).toInt,
        )
    },
  )
