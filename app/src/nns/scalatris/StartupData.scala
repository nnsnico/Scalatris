package nns.scalatris

import indigo.*
import indigoextras.geometry.BoundingBox
import indigo.shared.config.GameViewport
import indigo.shared.Outcome
import nns.scalatris.assets.Block
import nns.scalatris.assets.Font
import nns.scalatris.extensions._
import nns.scalatris.assets._

final case class StartUpData(
    viewConfig: ViewConfig,
    staticAssets: StaticAssets,
)

final case class StaticAssets(
    stage: Seq[Shape.Box],
    piece: Piece,
)

object StartUpData:

  def initialize(viewConfig: ViewConfig): Outcome[Startup[StartUpData]] =
    Outcome(createStartupData(viewConfig).toStartup)

  private[this] def createStartupData(viewConfig: ViewConfig): Option[StartUpData] =
    val blockSize = viewConfig.gridSquareSize
    val block     = IKind(blockSize)
    Some(
      StartUpData(
        viewConfig = viewConfig,
        staticAssets = StaticAssets(
          stage = Grid(viewConfig.gridSquareSize).createStageBounds(
            gridSize = viewConfig.gridSize,
            offsetX = viewConfig.gridSquareSize,
            offsetY = viewConfig.gridSquareSize
          ),
          piece = block,
        ),
      ),
    )
