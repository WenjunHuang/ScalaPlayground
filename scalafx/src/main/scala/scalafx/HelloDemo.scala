package scalafx

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.Includes._
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Rectangle

object HelloDemo extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title.value = "Hello Stage"
    width = 600
    height = 450
    val rect = new Rectangle {
      x = 25
      y = 40
      width = 100
      height = 100
      fill <== when(hover) choose (Green) otherwise (Red)
    }

    val text = new Label {
      x = 25
      y = 50
      text <== when(rect.hover && !disabled) choose "enabled" otherwise "disabled"

    }

    scene = new Scene {
      fill = LightGreen
      content = new HBox {
        children = Seq(
          new Button("Click Me") {
            style = "{-fx-border-width:0}"
          },
          rect,
          text)
      }
    }
  }

}
