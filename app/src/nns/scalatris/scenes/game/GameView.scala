package nns.scalatris.scenes.game

import indigo.*
import indigo.logger._
import indigoextras.geometry.Vertex
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
              .map(p =>
                drawPiece(
                  piece = p,
                  stageSizeOffSet = viewConfig.stageSize.position,
                ),
              )
              .orNull,
          ),
        ),
    )

  private def drawPiece(
      piece: Piece,
      stageSizeOffSet: Vertex,
  ): Batch[Graphic[_]] = Batch.fromSeq(
    piece.current.map { pos =>
      piece
        .material
        .bitmap
        .moveTo(
          x = ((piece.material.size * pos.x) + stageSizeOffSet.x).toInt,
          y = ((piece.material.size * pos.y) + stageSizeOffSet.y).toInt,
        )
    },
  )
