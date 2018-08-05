import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.badlogic.gdx.{Application, ApplicationListener, Gdx}
import com.badlogic.gdx.graphics.{Color, Pixmap, Texture}
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import org.lwjgl.opengl.GL11

object GraphicsDemo extends App with ApplicationListener {
  val config = new LwjglApplicationConfiguration
  config.title = "GraphicsDemo"
  config.useGL30 = false
  config.width = 800
  config.height = 600

  Gdx.app = new LwjglApplication(GraphicsDemo,config)
  Gdx.app.setLogLevel(Application.LOG_INFO)

  override def resume(): Unit = {}

  override def pause(): Unit = {}

  override def create(): Unit = {
    batch = new SpriteBatch()
//    texture = new Texture(Gdx.files.internal("jet.png"))

    pixmap = new Pixmap(256,128,Pixmap.Format.RGBA8888)
    pixmap.setColor(Color.RED)
    pixmap.fill()
    pixmap.setColor(Color.BLACK)
    pixmap.drawLine(0,0,pixmap.getWidth -1,pixmap.getHeight -1)
    pixmap.drawLine(0,pixmap.getHeight-1,pixmap.getWidth-1,0)
    pixmap.setColor(Color.YELLOW)
    pixmap.drawCircle(pixmap.getWidth/2,pixmap.getHeight/2,pixmap.getHeight/2 -1)

    texture = new Texture(pixmap)
    pixmap.dispose()
    sprite = new Sprite(texture)


  }

  override def resize(width: Int, height: Int): Unit = {}

  override def dispose(): Unit = {
    batch.dispose()
    texture.dispose()
  }

  override def render(): Unit = {
    Gdx.gl.glClearColor(1,1,1,1)
    Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT)

    batch.begin()
    sprite.draw(batch)
    sprite.setPosition(Gdx.graphics.getWidth/2,
                       Gdx.graphics.getHeight/2)
    sprite.draw(batch)
    batch.end()
  }

  var batch:SpriteBatch = _
  var pixmap:Pixmap = _
  var texture:Texture = _
  var sprite:Sprite = _
}
