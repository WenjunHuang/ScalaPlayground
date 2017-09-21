package scalafx

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.control.{CheckMenuItem, Menu, MenuBar, MenuItem}
import scalafx.scene.layout.{BorderPane, VBox}

object MenuBarDemo extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    width = 640
    height = 480
    scene = new Scene {
      root =new BorderPane {
        top = new MenuBar {
          useSystemMenuBar = true
          menus = List(
           new Menu("Main") {
             items = Seq(
               new MenuItem("Load"),
               new CheckMenuItem("Underline")
             )
           },
           new Menu("Edit") {
             items = Seq(
               new Menu("Convert") {
                 items = Seq(
                   new MenuItem("PDF"),
                   new MenuItem("PNG")
                 )
               },
               new MenuItem("Rotate")
             )
           }
          )
        }

      }
    }
  }

}
