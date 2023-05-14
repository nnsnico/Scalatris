package nns.scalatris.assets

import indigo.*
import indigo.shared.*
import indigo.shared.datatypes.Vector2
import indigo.shared.materials.Material.Bitmap
import indigo.shared.scenegraph.Graphic
import nns.scalatris.GridSquareSize
import nns.scalatris.GridSquareSize._

object Block extends Assets[Material.Bitmap]:

  override protected val assetName: AssetName = AssetName("block")

  override protected[assets] val material = Material.Bitmap(assetName)

  override protected[assets] def path(baseUrl: String): AssetType =
    AssetType.Image(
      assetName,
      AssetPath(baseUrl + "assets/" + assetName + ".png"),
    )

  def materials(size: GridSquareSize): Seq[BlockMaterial] = Seq(
    Blue(size),
    Green(size),
    Red(size),
    Orange(size),
    Purple(size),
    SkyBlue(size),
    Yellow(size),
  )

sealed trait BlockMaterial:
  def bitmap: Graphic[Material.Bitmap]

sealed class BaseMaterial(size: GridSquareSize) extends BlockMaterial:
  final protected val bitmapSize = 64

  override def bitmap = Graphic[Material.Bitmap](
    x = 0,
    y = 0,
    width = size.toInt,  // same as height
    height = size.toInt, // same as width
    depth = 2,
    material = Block.material,
  )

final case class Blue(size: GridSquareSize)
    extends BaseMaterial(size)
    with BlockMaterial

final case class Green(size: GridSquareSize)
    extends BaseMaterial(size)
    with BlockMaterial:

  override val bitmap = super
    .bitmap
    .withCrop(
      x = bitmapSize,
      y = 0,
      width = size.toInt,
      height = size.toInt,
    )

final case class Red(size: GridSquareSize)
    extends BaseMaterial(size)
    with BlockMaterial:

  override val bitmap = super
    .bitmap
    .withCrop(
      x = bitmapSize * 2,
      y = 0,
      width = size.toInt,
      height = size.toInt,
    )

final case class Orange(size: GridSquareSize)
    extends BaseMaterial(size)
    with BlockMaterial:

  override val bitmap = super
    .bitmap
    .withCrop(
      x = 0,
      y = bitmapSize,
      width = size.toInt,
      height = size.toInt,
    )

final case class Purple(size: GridSquareSize)
    extends BaseMaterial(size)
    with BlockMaterial:

  override val bitmap = super
    .bitmap
    .withCrop(
      x = bitmapSize,
      y = bitmapSize,
      width = size.toInt,
      height = size.toInt,
    )

final case class SkyBlue(size: GridSquareSize)
    extends BaseMaterial(size)
    with BlockMaterial:

  override val bitmap = super
    .bitmap
    .withCrop(
      x = bitmapSize * 2,
      y = bitmapSize,
      width = size.toInt,
      height = size.toInt,
    )

final case class Yellow(size: GridSquareSize)
    extends BaseMaterial(size)
    with BlockMaterial:

  override val bitmap = super
    .bitmap
    .withCrop(
      x = 0,
      y = bitmapSize * 2,
      width = size.toInt,
      height = size.toInt,
    )
