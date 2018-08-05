import com.badlogic.gdx.backends.lwjgl.{LwjglApplication, LwjglApplicationConfiguration}
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.{ApplicationListener, Gdx}
import com.badlogic.gdx.graphics.{OrthographicCamera, Texture}
import com.badlogic.gdx.graphics.g2d.{Sprite, SpriteBatch}
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.input.GestureDetector.GestureListener
import com.badlogic.gdx.math.Vector2
import org.lwjgl.opengl.GL11

object CameraDemo extends App with ApplicationListener with GestureListener{
  var camera:OrthographicCamera = _
  var batch:SpriteBatch = _
  var texture:Texture = _
  var sprite:Sprite = _

  override def resume(): Unit = {}

  override def pause(): Unit = {}

  override def create(): Unit = {
    camera = new OrthographicCamera(1280,720)
    batch = new SpriteBatch()
    texture = new Texture(Gdx.files.internal("toronto2948wide.jpg"))
    texture.setFilter(TextureFilter.Linear,TextureFilter.Linear)

    sprite = new Sprite(texture)
    sprite.setOrigin(0,0)
    sprite.setPosition(-sprite.getWidth/2,-sprite.getHeight/2)

    Gdx.input.setInputProcessor(new GestureDetector(this))
  }

  override def resize(width: Int, height: Int): Unit = {}

  override def dispose(): Unit = {
    batch.dispose()
    texture.dispose()
  }

  override def render(): Unit = {
    Gdx.gl.glClearColor(1,1,1,1)
    Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT)
    batch.setProjectionMatrix(camera.combined)
    batch.begin()
    sprite.draw(batch)
    batch.end( )

  }
override def panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean = false
override def fling(velocityX: Float, velocityY: Float, button: Int): Boolean = false
override def pinchStop(): Unit = {}
override def pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean = {
  camera.translate(deltaX,0)
  camera.update()
  false
}
override def pinch(initialPointer1: Vector2,
                   initialPointer2: Vector2,
                   pointer1: Vector2,
                   pointer2: Vector2): Boolean = false
override def tap(x: Float, y: Float, count: Int, button: Int): Boolean = false
override def touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean = false
override def longPress(x: Float, y: Float): Boolean = false

  override def zoom(initialDistance: Float, distance: Float): Boolean = false

  val config = new LwjglApplicationConfiguration
  config.title = "camera"
  config.useGL30 = false
  config.width = 1280
  config.height = 720

  Gdx.app = new LwjglApplication(CameraDemo,config)
}
