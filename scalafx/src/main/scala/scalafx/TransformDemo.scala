package scalafx


import javafx.scene.layout.Border

import scalafx.application.JFXApp
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.transform.{Rotate, Shear, Transform, Translate}
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.scene.layout._

object TransformDemo extends JFXApp {
  val myPane = new StackPane {
    border = new Border(new BorderStroke(Color.Black,
                                         BorderStrokeStyle.Solid,
                                         CornerRadii.Empty,
                                         BorderWidths.Default))
  }
  def useTransforms(transforms:Transform*): Unit = {
    myPane.children.clear()

    val origin = new Rectangle{
      width = 40
      height = 40
      style =
        """
          |-fx-stroke:blue;
          |-fx-fill:darkgrey;
        """.stripMargin
      opacity = 0.5
    }
    myPane.children.add(origin)

    var usedTransforms = List[Transform]()
    transforms.foreach{t⇒
      usedTransforms :+= t
      val r = new Rectangle {
        width = 40
        height = 40
        style =
          """
            |-fx-stroke: blue;
            |-fx-fill: transparent;
          """.stripMargin
        opacity = usedTransforms.size
        transforms = usedTransforms
      }
      myPane.children.add(r)
    }

  }

  val rotation = new Rotate(45)
  val translate = new Translate(24,24)
  val shearing = new Shear(0,1)
  stage = new JFXApp.PrimaryStage {
    title = "App"
    width = 300
    height = 200
    scene = new Scene {
      root = new BorderPane {
        center = myPane
        bottom = new HBox {
          spacing = 6
          padding = Insets(12)
          alignment = Pos.Center
          children = Seq(
            new Button("Alternative 1") {
              onAction = {_⇒
                useTransforms(rotation, translate)
              }
            },
            new Button("Alternate 2") {
              onAction = {_⇒useTransforms(translate,rotation)}
            },
            new Button("Alternate 3"){
              onAction = {_⇒useTransforms(rotation,shearing,translate)}
            }
          )
        }
      }
    }

  }

}
