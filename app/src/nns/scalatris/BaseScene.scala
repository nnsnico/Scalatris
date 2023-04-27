package nns.scalatris

import indigo.scenes.*
import indigo.shared.events.GlobalEvent
import indigo.shared.Outcome

trait BaseScene[StartupData, Model, ViewModel] extends Scene[StartupData, Model, ViewModel]:
  type SceneModel = Controller
  type Controller

