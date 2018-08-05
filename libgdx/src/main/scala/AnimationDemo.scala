import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.badlogic.gdx.{ApplicationListener, Gdx}
import com.badlogic.gdx.graphics.g2d.{Animation, SpriteBatch, TextureAtlas, TextureRegion}
import org.lwjgl.opengl.GL11

object AnimationDemo extends App with ApplicationListener{
  var batch:SpriteBatch = _
  var textureAtlas:TextureAtlas = _
  var textureMoveAtlas:TextureAtlas = _
  var animation:Animation[TextureRegion] = _

  var elapsedTime = 0.0f
  override def resume(): Unit = {}

  override def pause(): Unit = {}

  override def create(): Unit = {
    batch = new SpriteBatch()
    textureAtlas = new TextureAtlas(Gdx.files.internal("spritesheet.atlas"))
    animation = new Animation(1/30f,textureAtlas.getRegions)

    textureMoveAtlas = new TextureAtlas(Gdx.files.internal("spritesheet.atlas"))
  }

  override def resize(width: Int, height: Int): Unit = {}

  override def dispose(): Unit = {
    batch.dispose()
    textureAtlas.dispose()
  }

  override def render(): Unit = {
    Gdx.gl.glClearColor(0,0,0,1)
    Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT)

    batch.begin()
    elapsedTime = elapsedTime + Gdx.graphics.getDeltaTime
    batch.draw(animation.getKeyFrame(elapsedTime,true),
               0,0)
    batch.end()

  }

  val config = new LwjglApplicationConfiguration
  config.title = "Animation demo"
  config.width = 640
  config.height = 480
  config.useGL30 = false

  val app = new LwjglApplication(AnimationDemo,config)
  Gdx.app = app
}
