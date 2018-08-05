
import com.badlogic.gdx.{ApplicationListener, Gdx}
import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch, TextureAtlas}
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.Timer.Task
import org.lwjgl.opengl.GL11


object TextureAtlasDemo extends App with ApplicationListener {
  override def resume(): Unit = {}

  override def pause(): Unit = {}

  override def create(): Unit = {
    batch = new SpriteBatch()
    textureAtlas = new TextureAtlas(Gdx.files.internal("spritesheet.atlas"))
    val region = textureAtlas.findRegion("0001")

    sprite = new Sprite(region)
    sprite.setPosition(120, 100)
    sprite.scale(2.5f)
    Timer.schedule(() => {
      currentFrame = currentFrame + 1
      if (currentFrame > 20)
        currentFrame = 1

      currentAtlasKey =f"$currentFrame%04d"
      sprite.setRegion(textureAtlas.findRegion(currentAtlasKey))
    }, 0,1 / 30.0f)

  }

  override def resize(width: Int, height: Int): Unit = {

  }

  override def dispose(): Unit = {
    batch.dispose()
    textureAtlas.dispose()
  }

  override def render(): Unit = {
    Gdx.gl.glClearColor(0,0,0,1)
    Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT)

    batch.begin()
    sprite.draw(batch)
    batch.end()
  }

  var batch: SpriteBatch = _
  var textureAtlas: TextureAtlas = _
  var sprite: Sprite = _
  var currentFrame = 1
  var currentAtlasKey = "0001"

  val config = new LwjglApplicationConfiguration
  config.width = 400
  config.height = 300
  config.useGL30 = false

  Gdx.app = new LwjglApplication(TextureAtlasDemo, config)
}
