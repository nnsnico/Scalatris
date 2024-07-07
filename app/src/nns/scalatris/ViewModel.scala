package nns.scalatris

import indigo.*
import indigo.scenes.*
import nns.scalatris.assets.Grid

final case class ViewModel(title: Group, stage: Group)

object ViewModel:

  def init(startupData: StartUpData): ViewModel = ViewModel(
    title = Group.empty,
    stage = Group(
      Batch.fromSeq(
        Grid(
          gridSize = startupData.viewConfig.gridSquareSize,
          stageSize = startupData.viewConfig.stageSize,
        ).createStageBounds,
      ),
    ),
  )
