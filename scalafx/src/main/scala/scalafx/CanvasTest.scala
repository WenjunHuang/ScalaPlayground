package scalafx

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.effect.DropShadow
import scalafx.scene.paint._
import scalafx.scene.transform.{Scale, Transform}

object CanvasTest extends JFXApp {
  val canvas = new Canvas(200, 200)
  val gc = canvas.graphicsContext2D


  val scale = new Scale(1.0,-1.0)
  canvas.transforms.add(scale)
//  canvas.translateX = 0
//  canvas.translateY = 0

  gc.beginPath()
  gc.moveTo(50, 50)
  gc.bezierCurveTo(150,20,150,150,75,150)
  gc.closePath()

  gc.fill = new RadialGradient(0, 0, 0.5, 0.5, 0.1, true, CycleMethod.Reflect, List(Stop(0.0, Color.Red), Stop(1.0, Color.Yellow)))
  gc.fillPath()
  val lg = new LinearGradient(0, 0, 1, 1, true, CycleMethod.Reflect, List(Stop(0.0, Color.Blue), Stop(1.0, Color.Green)))
  gc.stroke = lg
  gc.lineWidth = 20
  gc.strokePath()

  /**
    * Draws a radial gradient on the Canvas object, which appears as a series of
    * circles radiating outward. This demo uses Red and YELLOW by default.
    */
  //  drawDropShadow(Color.Gray, Color.Blue, Color.Green, Color.Red)
  gc.applyEffect(new DropShadow(20, 20, 0, Color.Gray))
  gc.applyEffect(new DropShadow(20, 0, 20, Color.Blue))
  gc.applyEffect(new DropShadow(20, -20, 0, Color.Green))
  gc.applyEffect(new DropShadow(20, 0, -20, Color.Red))

  stage = new PrimaryStage {
    title = "Canvas Test"
    scene = new Scene(400, 400) {
      content = canvas
    }
  }

}
