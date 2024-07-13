package nns.scalatris

import indigo.*
import indigo.shared.*
import nns.scalatris.assets.*
import nns.scalatris.extensions.Option.*

final case class StartUpData(
    viewConfig: ViewConfig,
    staticAssets: StaticAssets,
)

final case class StaticAssets(
    blockMaterial: Seq[BlockMaterial],
)

object StartUpData:

  def initialize(viewConfig: ViewConfig): Outcome[Startup[StartUpData]] =
    Outcome(createStartupData(viewConfig).toStartup)

  private def createStartupData(
      viewConfig: ViewConfig,
  ): Option[StartUpData] =
    Some(
      StartUpData(
        viewConfig = viewConfig,
        staticAssets = StaticAssets(
          blockMaterial = Block.materials(viewConfig.gridSquareSize),
        ),
      ),
    )
