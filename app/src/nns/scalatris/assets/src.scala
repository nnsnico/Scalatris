package nns.scalatris.assets

import indigo.shared.assets.{AssetName, AssetType}
import indigo.shared.materials.Material


private trait Assets[T <: Material]:
  protected val assetName: AssetName
  protected[assets] val material: T
  protected[assets] def path(baseUrl: String): AssetType

def allAssets(baseUrl: String): Set[AssetType] = Set(
  Block.path(baseUrl),
  Font.path(baseUrl),
)
