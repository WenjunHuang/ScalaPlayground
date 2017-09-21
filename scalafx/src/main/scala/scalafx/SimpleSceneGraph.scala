package scalafx

import javafx.scene.input.KeyCode
import javafx.scene.layout._

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.geometry.{Insets, NodeOrientation, Pos}
import scalafx.scene.{Cursor, Scene}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.Image
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color

object SimpleSceneGraph extends JFXApp {
  val hbox = new HBox {
    background = null
    alignment = Pos.Center
    border = new Border(new BorderStroke(Color.Black,
                                         BorderStrokeStyle.SOLID,
                                         CornerRadii.EMPTY,
                                         BorderWidths.DEFAULT))

    children = List(
      new Button {
        text = "Button 1"
      },
      new Button {
        text = "Button 2"
      },
      new Button {
        text = "Button 3"
      },
    )
  }
  stage = new JFXApp.PrimaryStage {
    width = 640
    height = 480
    title = "Simple Scene Graph"
    scene = new Scene {
      cursor = Cursor.Hand
      fill = Color.White
      root = hbox
      onZoom = {event ⇒
        hbox.scaleX = hbox.getScaleX * event.getZoomFactor
        hbox.scaleY = hbox.getScaleY * event.getZoomFactor
      }
      onRotate = {event ⇒
        hbox.rotate = hbox.getRotate + event.getAngle
      }
    }
  }
}
