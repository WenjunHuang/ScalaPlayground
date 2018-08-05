package scalafx

import javafx.css.{ParsedValue, StyleConverter}
import javafx.scene.text.Font
import javafx.util.Duration

object DurationStyleConverter extends StyleConverter[String,Duration] {
  override def convert(value:ParsedValue[String,Duration],font:Font): Duration = {
    val cssProperty = value.getValue
    if (cssProperty.endsWith("ms")){
      Duration.millis(cssProperty.substring(0,cssProperty.length -2).toLong)
    }else if (cssProperty.endsWith("s"))
      Duration.millis(cssProperty.substring(0,cssProperty.length -1).toLong)
    else
      Duration.millis(cssProperty.toLong)
  }
}
