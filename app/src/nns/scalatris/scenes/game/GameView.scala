package nns.scalatris.scenes.game

import cats.syntax.all.*
import indigo.*
import indigo.shared.*
import indigoextras.geometry.Vertex
import nns.scalatris.ViewConfig
import nns.scalatris.assets.Font as GameFont
import nns.scalatris.scenes.game.model.Piece

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
            BindingKey("game-ui-main"),
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
