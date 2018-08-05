import java.nio.{ByteBuffer, FloatBuffer}

import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl.util.FPSAnimator
import com.jogamp.opengl.{GL4, GLAutoDrawable, GLContext, GLEventListener, _}
import javax.swing.JFrame
import utility._

object HelloWorld extends App {
  val mainFrame = new HelloWorldFrame
}

class HelloWorldFrame extends JFrame with GLEventListener {
  var renderingProgram: Int   = -1
  val vao                     = Array.ofDim[Int](1)
  var vx              : Float = 0.0f
  var inc                     = 0.01f

  setTitle("Hello World")
  setSize(600, 400)
  setLocation(200, 200)
  val glp      = GLProfile.getMaxProgrammableCore(true)
  val caps     = new GLCapabilities(glp)
  val myCanvas = new GLCanvas(caps)
  myCanvas.addGLEventListener(this)
  add(myCanvas)
  setVisible(true)
  val animtr = new FPSAnimator(myCanvas, 50)
  animtr.start()

  override def reshape(glAutoDrawable: GLAutoDrawable, i: Int, i1: Int, i2: Int, i3: Int): Unit = {}

  override def init(glAutoDrawable: GLAutoDrawable): Unit = {
    val gl = GLContext.getCurrentGL.asInstanceOf[GL4]
    renderingProgram = createShaderProgram()
    gl.glGenVertexArrays(vao.length, vao, 0)
    gl.glBindVertexArray(vao(0))
  }

  def createShaderProgram(): Int = {
    val gl = GLContext.getCurrentGL.asInstanceOf[GL4]
    val vshaderSource = Array(
        "#version 410 \n",
        "uniform float offset; \n",
        "void main(void) \n",
        "{ if (gl_VertexID == 0) gl_Position = vec4(0.25 + offset, -0.25,0.0,1.0); \n",
        "else if (gl_VertexID ==1)gl_Position = vec4(-0.25 + offset,-0.25,0.0,1.0); \n",
        "else gl_Position = vec4(0.25 + offset,0.25,0.0,1.0); \n",
        "}")

    val fshaderSource = Array(
      "#version 410 \n",
      "out vec4 color; \n",
      "void main(void) \n",
      "{ color = vec4(0.0,0.0,1.0,1.0); }\n"
    )

    val vShader = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER)
    gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0)
    gl.glCompileShader(vShader)
    checkShaderBuild(vShader)

    val fShader = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER)
    gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0)
    gl.glCompileShader(fShader)
    checkShaderBuild(fShader)

    val vfprogram = gl.glCreateProgram()
    gl.glAttachShader(vfprogram, vShader)
    gl.glAttachShader(vfprogram, fShader)
    gl.glLinkProgram(vfprogram)
    gl.glDeleteShader(vShader)
    gl.glDeleteShader(fShader)
    vfprogram
  }

  override def dispose(glAutoDrawable: GLAutoDrawable): Unit = {}

  override def display(glAutoDrawable: GLAutoDrawable): Unit = {
    val gl = GLContext.getCurrentGL.asInstanceOf[GL4]
    gl.glUseProgram(renderingProgram)

    val bkg = Array(0.0f, 0.0f, 0.0f, 1.0f)
    val bkgBuffer = Buffers.newDirectFloatBuffer(bkg)
    gl.glClearBufferfv(GL2ES3.GL_COLOR, 0, bkgBuffer)

    vx += inc
    if (vx > 1.0f) inc = -0.01f
    if (vx < -1.0f) inc = 0.01f
    val offsetLoc = gl.glGetUniformLocation(renderingProgram, "offset")
    gl.glProgramUniform1f(renderingProgram, offsetLoc, vx)
    gl.glDrawArrays(GL.GL_TRIANGLES, 0, 3)
  }

}
