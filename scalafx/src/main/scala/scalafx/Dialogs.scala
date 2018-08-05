package scalafx

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ColorPicker, Label, TextField}
import scalafx.scene.layout.{ColumnConstraints, GridPane, Priority}
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.shape.Rectangle

object Dialogs extends JFXApp {
  var fileLabel: TextField = new TextField
  val colorFrame: Rectangle = new Rectangle {
    width = 20
    height = 20
  }
  stage = new JFXApp.PrimaryStage {
    title.value = "Hello Stage"
    scene = new Scene {
      root = new GridPane {
        margin = Insets(10,10,10,10)
        hgap = 10
        vgap = 10
        columnConstraints = Seq(
          new ColumnConstraints(){
            hgrow = Priority.Never
          },
          new ColumnConstraints(){
            hgrow = Priority.Always
          }
        )
        add(new Button("文件标准对话框实例") {
          onAction = { event ⇒
            val fileChooser = new FileChooser() {
              title = "open file dialog"
              extensionFilters.addAll(
                new ExtensionFilter("C++ files", "*.cpp"),
                new ExtensionFilter("C files", "*.c"),
                new ExtensionFilter("Head files", "*.h")
              )
            }
            val selected = Option(fileChooser.showOpenDialog(window.value))
            selected.foreach { f ⇒
              fileLabel.setText(f.getAbsolutePath)
            }
          }
        },0,0)
        add(fileLabel,1,0)
        add(new ColorPicker(){
          colorFrame.fill <== value
        },0,1)
        add(colorFrame,1,1)
      }
    }
  }
}
