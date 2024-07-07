package nns.scalatris.scenes

import indigo.*
import indigo.scenes.*
import nns.scalatris.ViewConfig
import nns.scalatris.assets.BlockMaterial

final private val TICK_FRAME_SECONDS = 0.1f
final private val FALL_DOWN_SECONDS  = 1.0f

private class BaseSceneModel(
  val tickDelay: Seconds,
  val lastUpdated: Seconds,
)

