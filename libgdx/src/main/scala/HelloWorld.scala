import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.badlogic.gdx.{Application, ApplicationListener, Gdx}
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import org.lwjgl.opengl.{GL11, GL15, GL41}

object HelloWorld extends ApplicationListener {
  var batch:SpriteBatch = null
  var font:BitmapFont = null

  override def resume(): Unit = {}

  override def pause(): Unit = {}

  override def create(): Unit = {
    batch = new SpriteBatch()
    font = new BitmapFont()
    font.setColor(Color.RED)
  }

  override def resize(width: Int, height: Int): Unit = {}

  override def dispose(): Unit = {
    batch.dispose()
    font.dispose()
  }

  override def render(): Unit = {
    Gdx.gl.glClearColor(1,1,1,1)
    Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT)

    batch.begin()
    font.draw(batch,"Hello World",200,200)
    batch.end()
  }
}

object HelloWorldDesktop extends App{
  val config = new LwjglApplicationConfiguration
  config.title = "HelloWorld"
//  config.useHDPI = true
  config.useGL30 = false
  config.width = 800
  config.height = 600

  Gdx.app = new LwjglApplication(HelloWorld,config)
  Gdx.app.setLogLevel(Application.LOG_INFO)
}