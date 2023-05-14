import mill._
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._
// mill-scalafix
import $ivy.`com.goyeau::mill-scalafix::0.2.9`
import com.goyeau.mill.scalafix.ScalafixModule
// indigo
import $ivy.`io.indigoengine::mill-indigo:0.14.0`, millindigo._

object app extends ScalaJSModule with MillIndigo with ScalafixModule {
  def scalaVersion   = "3.2.0"
  def scalaJSVersion = "1.11.0"

  val gameAssetsDirectory: os.Path = os.pwd / "assets"
  val showCursor: Boolean          = true
  val title: String                = "Scalatris"

  val windowStartWidth: Int =
    720 // Width of Electron window, used with `indigoRun`.

  val windowStartHeight: Int =
    500 // Height of Electron window, used with `indigoRun`.

  val disableFrameRateLimit: Boolean = false
  val backgroundColor: String        = "black"
  val electronInstall                = indigoplugin.ElectronInstall.Global

  def buildGame() = T.command {
    T {
      compile()
      fastOpt()
      fastLinkJS()
      indigoBuild()()
    }
  }

  def runGame() = T.command {
    T {
      compile()
      fastOpt()
      fastLinkJS()
      indigoRun()()
    }
  }

  val indigoVersion = "0.14.0"
  val catsVersion   = "2.9.0"
  val mouseVersion  = "1.2.1"

  def ivyDeps = Agg(
    ivy"io.indigoengine::indigo::$indigoVersion",
    ivy"io.indigoengine::indigo-extras::$indigoVersion",
    ivy"io.indigoengine::indigo-json-circe::$indigoVersion",
    ivy"org.typelevel::cats-core:$catsVersion",
    ivy"org.typelevel::mouse:$mouseVersion",
  )

  val scalaFixOrganizeImportsVersion = "0.6.0"

  def scalafixIvyDeps = Agg(
    ivy"com.github.liancheng::organize-imports:$scalaFixOrganizeImportsVersion",
  )

}
