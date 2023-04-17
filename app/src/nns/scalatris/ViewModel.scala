package nns.scalatris

import indigo.*
import nns.scalatris.assets.Grid
import nns.scalatris.assets.Piece
import nns.scalatris.assets.Position

final case class ViewModel(stage: Group)

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
  )
