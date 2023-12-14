package nns.scalatris.scenes.game

import cats.syntax.all._
import indigo.*
import indigo.shared.collections.Batch
import indigo.shared.datatypes.Vector2
import indigo.shared.scenegraph.TextBox
import indigoextras.geometry.Vertex
import nns.scalatris.ViewConfig
import nns.scalatris.assets._
import nns.scalatris.assets.{Font => GameFont}
import nns.scalatris.model.{GlobalModel, Piece, PieceState}
import nns.scalatris.types.GridSquareSize

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
              .fold(
                e => drawDebugLog(viewConfig.viewport, e.toString),
                v =>
                  drawPiece(
                    piece = v,
                    stageSizeOffSet = viewConfig.stageSize.position,
                  ),
              ) ++ Batch.fromSet(
              model
                .stageMap
                .flatMap(p => drawPiece(p, viewConfig.stageSize.position).toSet),
            ),
          ),
        ),
    )

  private def drawPiece(
      piece: Piece,
      stageSizeOffSet: Vertex,
  ): Batch[Graphic[_]] = Batch.fromSeq(
    piece.currentPosition.map { pos =>
      piece
        .material
        .bitmap
        .moveTo(
          x = ((piece.material.size * pos.x) + stageSizeOffSet.x).toInt,
          y = ((piece.material.size * pos.y) + stageSizeOffSet.y).toInt,
        )
    },
  )

  private def drawDebugLog(
      viewport: GameViewport,
      text: String,
  ): Batch[SceneNode] =
    Batch(
      GameFont.toText(
        text.grouped(20).reduce { case (z, s) => z + "\n" + s },
        0,
        0,
      ),
    )
