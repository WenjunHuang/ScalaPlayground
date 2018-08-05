import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.badlogic.gdx.{ApplicationListener, Gdx, Input}
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import org.lwjgl.opengl.GL11

object InputDemo extends App with ApplicationListener {
  var batch:SpriteBatch = _
  var texture:Texture = _
  var sprite:Sprite = _

  override def resume(): Unit = {}

  override def pause(): Unit = {}

  override def create(): Unit = {
    val w = Gdx.graphics.getWidth
    val h = Gdx.graphics.getHeight
    batch = new SpriteBatch()

    texture = new Texture(Gdx.files.internal("0001.png"))
    sprite = new Sprite(texture)
    sprite.setPosition(w / 2 - sprite.getWidth/2,
                       h / 2 - sprite.getHeight)
  }

  override def resize(width: Int, height: Int): Unit = {}

  override def dispose(): Unit = {
    batch.dispose()
    texture.dispose()
  }

  override def render(): Unit = {
    Gdx.gl.glClearColor(1,1,1,1)
    Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT)

    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
      if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
        sprite.translateX(-1f)
      else
        sprite.translateX(-10.0f)
    }
    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
      if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
        sprite.translateX(1f)
      else
        sprite.translateX(10.0f)
    }

    batch.begin()
    sprite.draw(batch)
    batch.end()
  }

  val config = new LwjglApplicationConfiguration
  config.title = "Input demo"
  config.width = 640
  config.height = 480
  config.useGL30 = false

  val app = new LwjglApplication(InputDemo,config)
  Gdx.app = app
}
