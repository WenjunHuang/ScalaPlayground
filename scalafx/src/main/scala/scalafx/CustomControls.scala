package scalafx

import java.util
import javafx.beans.property.{ObjectProperty, ObjectPropertyBase, SimpleObjectProperty}
import javafx.css.{CssMetaData, SimpleStyleableObjectProperty, Styleable, StyleableObjectProperty}
import javafx.event.{ActionEvent, Event, EventHandler, EventType}
import javafx.scene.control.{Control, Skin, SkinBase, Skinnable}
import javafx.scene.paint.Paint

import com.sun.javafx.css.converters.PaintConverter

import scala.beans.BeanProperty
import scala.util.Random
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.css.StyleConverter
import scalafx.geometry.{HPos, Insets, VPos}
import scalafx.scene.Scene
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.{ClosePath, LineTo, MoveTo, Path}
import scala.collection.JavaConverters._


object TriangleButton {
  val BackgroundFill:CssMetaData[TriangleButton,Paint] =
    new CssMetaData[TriangleButton,Paint]("-fx-triangle-fill",
                                          StyleConverter.paintConverter,
                                          Color.Black) {
      override def isSettable(styleable: TriangleButton):Boolean =
         !styleable.backgroundFillProperty.isBound

      override def getStyleableProperty(styleable: TriangleButton) =
        styleable.backgroundFillProperty
    }

  val Styleables = Control.getClassCssMetaData.asScala.toList :+ BackgroundFill
}

class TriangleButton extends Control {
  val backgroundFillProperty: StyleableObjectProperty[Paint] =
    new SimpleStyleableObjectProperty[Paint](TriangleButton.BackgroundFill,
                                             this,
                                             "backgroundFill",
                                             Color.DarkGrey)

  def backgroundFill = backgroundFillProperty.get
  def backgroundFill_=(color:Color) = backgroundFillProperty.setValue(color)

  val onActionProperty:ObjectProperty[EventHandler[ActionEvent]] = new ActionProperty(this)

  def onAction_=(action:EventHandler[ActionEvent]) = {
    onActionProperty.set(action)
  }

  def onAction = onActionProperty.get()

  override def getControlCssMetaData: java.util.List[CssMetaData[_ <: Styleable, _]] =
    TriangleButton.Styleables.asJava

  override def createDefaultSkin(): Skin[_ <: Skinnable] = new TriangleButtonSkin(this)


  // 必须要将javaFX的setEventHandler方法包起来，否则scala编译器会报类型推导错误
  // 这个问题暂时只在这里出现，如果新建一个相同的java方法是不会报错的
  def setHandler(action:EventHandler[ActionEvent]):Unit ={
    setEventHandler(ActionEvent.ACTION,action)
  }

  class ActionProperty(button:TriangleButton) extends SimpleObjectProperty[EventHandler[ActionEvent]](button,"onAction") {
    override def invalidated(): Unit = {
      setHandler(get())
    }
  }
}

class TriangleButtonSkin(control: TriangleButton) extends SkinBase[TriangleButton](control) {
  var triangle:Option[Path] = None
  var _invalidTriangle = true

  control.widthProperty().onChange{_invalidTriangle = true}
  control.heightProperty().onChange{_invalidTriangle = true}
  control.backgroundFillProperty.onChange(updateTriangleColor)


  def updateTriangleColor():Unit ={
    triangle.foreach{t⇒
      t.setFill(getSkinnable.backgroundFill)
    }
  }

  def updateTriangle(width:Double,height:Double) = {
    triangle.foreach{t⇒getChildren.remove(t)}

    val path = new Path() {
      elements = Seq(
        MoveTo(width / 2, 0),
        LineTo(width, height),
        LineTo(0, height),
        new ClosePath()
      )
      stroke = Color.Black
      fill = getSkinnable.backgroundFillProperty.get()
    }

    path.onMouseClicked = {e⇒
      getSkinnable.fireEvent(new ActionEvent())
    }

    getChildren.add(path)
    triangle = Some(path)

  }

  override def layoutChildren(contentX: Double, contentY: Double, contentWidth: Double, contentHeight: Double): Unit ={
    if (_invalidTriangle){
      updateTriangle(contentWidth,contentHeight)
      _invalidTriangle = false
    }
    layoutInArea(triangle.get,contentX,contentY,contentWidth,
                 contentHeight, -1, HPos.Center,VPos.Center)
  }

  override def computeMinWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double) =
    20 + topInset + bottomInset

  override def computePrefWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double) =
    rightInset + leftInset + 200

  override def computeMaxWidth(height: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double) =
    computePrefWidth(height,topInset,rightInset,bottomInset,leftInset)

  override def computePrefHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double) =
    topInset + bottomInset + 200

  override def computeMinHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double) =
    20 + topInset + bottomInset

  override def computeMaxHeight(width: Double, topInset: Double, rightInset: Double, bottomInset: Double, leftInset: Double) =
    computePrefHeight(width,topInset,rightInset,bottomInset,leftInset)
}

object TriangleButtonDemo extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    width = 300
    height = 200
    scene = new Scene {
      root = new StackPane {
        children = new TriangleButton {button⇒
          id = "my_triangle_button"
          padding = Insets(20)
          onAction = {e ⇒
            button.backgroundFill = Color(Random.nextDouble(),
                                          Random.nextDouble(),
                                          Random.nextDouble(),
                                          Random.nextDouble())
          }
        }
      }
    }
  }
}