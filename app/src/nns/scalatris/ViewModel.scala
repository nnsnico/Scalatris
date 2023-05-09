package nns.scalatris

import indigo.*
import indigoextras.geometry.Vertex
import nns.scalatris.assets.Grid

final case class ViewModel(stage: Group)

object ViewModel:

  def init(startupData: StartUpData): ViewModel = ViewModel(
    Group(
      Batch.fromSeq(
        Grid(
          gridSize = startupData.viewConfig.gridSquareSize,
          stageSize = startupData.viewConfig.stageSize,
        ).createStageBounds,
      ),
    ),
  )
