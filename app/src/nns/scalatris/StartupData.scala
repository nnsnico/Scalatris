package nns.scalatris

import indigo.*
import indigo.shared.*
import indigoextras.geometry.BoundingBox
import nns.scalatris.assets.{Block, Font, _}
import nns.scalatris.extensions.Option._

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
