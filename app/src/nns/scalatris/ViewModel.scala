package nns.scalatris

import indigo.*
import nns.scalatris.assets.Grid
import nns.scalatris.assets.Piece
import nns.scalatris.assets.Position

final case class ViewModel(stage: Group, piece: Group)

object ViewModel:

  def init(startupData: StartUpData): ViewModel = ViewModel(
    Group(
      Batch.fromSeq(
        Grid(
          x = startupData.viewConfig.stageHorizontalCenter,
          gridSize = startupData.viewConfig.gridSquareSize,
          stageSize = startupData.viewConfig.stageSize,
        ).createStageBounds,
      ),
    ),
    Group(
      Batch.fromSeq(
        Piece.fromBlockMaterial(
          initialPosition = Position(startupData.viewConfig.stageHorizontalCenter, 0),
          blockSize = startupData.viewConfig.gridSquareSize,
          blockMaterials = startupData.staticAssets.blockMaterial,
        ),
      ),
    ),
  )
