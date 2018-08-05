import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl.{GLAutoDrawable, GLEventListener, _}
import javax.swing.JFrame
import utility._

object HelloTriangleIndexedApp extends App{
  new HelloTriangleIndexedFrame()
}
class HelloTriangleIndexedFrame extends JFrame with GLEventListener {
  setSize(600, 600)
  setTitle("Hello Triangle Index")

  val glp      = GLProfile.getMaxProgrammableCore(true)
  val caps     = new GLCapabilities(glp)
  val myCanvas = new GLCanvas(caps)
  myCanvas.addGLEventListener(this)
  add(myCanvas)
  setVisible(true)

  var program:Option[Int]  = None
  val vertices = Buffers.newDirectFloatBuffer(Array(0.5f, 0.5f, 0.0f,
                                                    0.5f, -0.5f, 0.0f,
                                                    -0.5f, -0.5f, 0.0f,
                                                    -0.5f, 0.5f, 0.0f))
  val indices  = Buffers.newDirectIntBuffer(Array(0, 1, 3,
                                                  1, 2, 3))
  val vao      = Array.ofDim[Int](1)
  val vbo      = Array.ofDim[Int](1)
  val ebo      = Array.ofDim[Int](1)

  def createProgram() = {
    val gl = currentGL()
    val vertexShader = buildShader(gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER), "Simple_V.glsl")
    val fragmentShader = buildShader(gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER), "Simple_F.glsl")
    val program = buildProgramAndLinkShaders(vertexShader, fragmentShader)

    gl.glDeleteShader(vertexShader)
    gl.glDeleteShader(fragmentShader)
    program
  }

  def createBuffers() = {
    val gl = currentGL()
    gl.glGenVertexArrays(vao.length, vao, 0)
    gl.glGenBuffers(1, vbo, 0)
    gl.glGenBuffers(1, ebo, 0)

    gl.glBindVertexArray(vao(0))
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo(0))
    gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.limit(), vertices, GL.GL_STATIC_DRAW)

    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo(0))
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.limit(), indices, GL.GL_STATIC_DRAW)

    gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 12, 0)
    gl.glEnableVertexAttribArray(0)
  }


  override def init(glAutoDrawable: GLAutoDrawable): Unit = {
    program = Some(createProgram())
    createBuffers()
  }

  override def dispose(glAutoDrawable: GLAutoDrawable): Unit = {}

  override def display(glAutoDrawable: GLAutoDrawable): Unit = {
    val gl = currentGL()
    gl.glClearColor(0.2f,0.3f,0.3f,1.0f)
    gl.glClear(GL.GL_COLOR_BUFFER_BIT)

    gl.glUseProgram(program.get)
    gl.glBindVertexArray(vao(0))
    gl.glDrawArrays(GL.GL_TRIANGLES,0,3)
//    gl.glDrawElements(GL.GL_TRIANGLES,6,GL.GL_UNSIGNED_INT,0)
  }

  override def reshape(glAutoDrawable: GLAutoDrawable, i: Int, i1: Int, i2: Int, i3: Int): Unit = {}
}
