import com.goyeau.mill.scalafix.ScalafixModule
import mill._
import mill.scalajslib._
import mill.scalajslib.api._
import mill.scalalib._
import millindigo._

import $ivy.`com.goyeau::mill-scalafix::0.3.1`
import $ivy.`io.indigoengine::mill-indigo:0.15.2`

object app extends ScalaJSModule with MillIndigo with ScalafixModule {
  def scalaVersion   = "3.3.0"
  def scalaJSVersion = "1.14.0"

  val indigoOptions: IndigoOptions = IndigoOptions
    .defaults
    .withTitle("Scalatris")
    .withWindowWidth(720)
    .withWindowHeight(500)
    .cursorVisible
    .electronLimitsFrameRate
    .withElectronInstallType(ElectronInstall.Global)
    .withBackgroundColor("black")
    .withAssetDirectory(os.RelPath.rel / "assets")

  val indigoGenerators: IndigoGenerators = IndigoGenerators("nns.scalatris")
    .listAssets("Assets", indigoOptions.assets)
    .generateConfig("DefaultConfig", indigoOptions)

  def buildGame() = T.command {
    T {
      compile()
      fastLinkJS()
      indigoBuild()()
    }
  }

  def runGame() = T.command {
    T {
      compile()
      fastLinkJS()
      indigoRun()()
    }
  }

  val indigoVersion = "0.15.2"
  val catsVersion   = "2.10.0"
  val mouseVersion  = "1.2.2"

  def ivyDeps = Agg(
    ivy"io.indigoengine::indigo::$indigoVersion",
    ivy"io.indigoengine::indigo-extras::$indigoVersion",
    ivy"io.indigoengine::indigo-json-circe::$indigoVersion",
    ivy"org.typelevel::cats-core::$catsVersion",
    ivy"org.typelevel::mouse::$mouseVersion",
  )

  object test extends ScalaJSTests with TestModule.Munit {
    val munitVersion = "0.7.29"

    def ivyDeps = Agg(
      ivy"org.scalameta::munit::$munitVersion",
    )

  }

}
