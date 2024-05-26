package nns.scalatris.scenes.title

import nns.scalatris.ViewConfig

final case class TitleController(viewConfig: ViewConfig, titleModel: TitleModel)

object TitleController:

  def init(viewConfig: ViewConfig): TitleController = TitleController(
    viewConfig = viewConfig,
    titleModel = TitleModel.init(),
  )
