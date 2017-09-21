package scalafx

import com.jfoenix.controls.JFXButton

import scalafx.application.JFXApp
import scalafx.geometry.Pos
import scalafx.scene.{Node, Scene}
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.control.MenuItem._
import scalafx.scene.control.ContextMenu._

object ContextMenuDemo extends JFXApp{

  stage = new JFXApp.PrimaryStage{
    width = 640
    height = 480
    scene = new Scene {
      root = new VBox {
        alignment = Pos.Center
        children = Seq[Node] (
          new JFXButton("Click me"){ button⇒
            setContextMenu(new ContextMenu {
              items += (
                new MenuItem("Rotate") {
                  onAction = {_⇒
                    button.setRotate(button.getRotate + 45.0)}
                },
                 new CheckMenuItem("Underline") {
                  selected <==>  button.underlineProperty()
                },
                new SeparatorMenuItem(),
              new RadioMenuItem("Radio")
              )
            })
          },
          new JFXButton("Next")
        )
      }
    }
  }

//  def contextMenu = new ContextMenu {
//      items +=(
//        new MenuItem("Rotate") {onAction = {e:ActionEvent⇒}}
//        )
//  }



}
