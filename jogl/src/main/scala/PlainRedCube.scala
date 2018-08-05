import java.io.{BufferedReader, InputStreamReader}

import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl._
import graphicslib3D.Matrix3D
import javax.swing.JFrame
import scala.io._
import utility._

object PlainRedCubeApp extends App {
  new PlainRedCubeFrame
}

class PlainRedCubeFrame extends JFrame with GLEventListener {
  setTitle("Plain Red Cube")
  setSize(600, 600)
  val glp      = GLProfile.getMaxProgrammableCore(true)
  val caps     = new GLCapabilities(glp)
  val myCanvas = new GLCanvas(caps)
  myCanvas.addGLEventListener(this)
  add(myCanvas)
  setVisible(true)

  val vao                                = Array.ofDim[Int](1)
  val vbo                                = Array.ofDim[Int](2)
  var renderingProgram: Option[Int]      = None
  var cameraX                            = 0.0f
  var cameraY                            = 0.0f
  var cameraZ                            = 8.0f
  var cubeLocX                           = 0.0f
  var cubeLocY                           = -2.0f
  var cubeLocZ                           = 0.0f
  var pMat            : Option[Matrix3D] = None

  override def init(glAutoDrawable: GLAutoDrawable): Unit = {
    val gl = GLContext.getCurrentGL.asInstanceOf[GL4]
    renderingProgram = Some(createShaderProgram())
    setupVertices()
    val aspect = myCanvas.getWidth.toFloat / myCanvas.getHeight.toFloat
    pMat = Some(perspective(60.0f, aspect, 0.1f, 1000.0f))

  }

  override def dispose(glAutoDrawable: GLAutoDrawable): Unit = {}

  override def display(glAutoDrawable: GLAutoDrawable): Unit = {
    val gl = GLContext.getCurrentGL.asInstanceOf[GL4]
    gl.glClear(GL.GL_DEPTH_BUFFER_BIT)
    gl.glUseProgram(renderingProgram.get)

    // build view matrix
    val vMat = new Matrix3D()
    vMat.translate(-cameraX, -cameraY, -cameraZ)

    // build model matrix
    val mMat = new Matrix3D()
    mMat.translate(cubeLocX, cubeLocY, cubeLocZ)

    val mvMat = new Matrix3D()
    mvMat.concatenate(vMat)
    mvMat.concatenate(mMat)

    val mvLoc = gl.glGetUniformLocation(renderingProgram.get, "mv_matrix")
    val projLoc = gl.glGetUniformLocation(renderingProgram.get, "proj_matrix")
    gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get.getFloatValues, 0)
    gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.getFloatValues, 0)

    // associate VBO with the corresponding vertex attribute in the vertex shader
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo(0))
    gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0)
    gl.glEnableVertexAttribArray(0)

    // adjust OpenGL settings and draw model
    gl.glEnable(GL.GL_DEPTH_TEST)
    gl.glDepthFunc(GL.GL_LEQUAL)
    gl.glDrawArrays(GL.GL_TRIANGLES, 0, 36)
  }

  def setupVertices() = {
    val gl = GLContext.getCurrentGL.asInstanceOf[GL4]
    val vertexPositions = Array(
      -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
      -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
      1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
      -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
      1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
      -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
      -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f,
      -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
      -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
      -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f,
      -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
      1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f
    )

    gl.glGenVertexArrays(vao.length, vao, 0)
    gl.glBindVertexArray(vao(0))
    gl.glGenBuffers(vbo.length, vbo, 0)
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo(0))
    val vertBuf = Buffers.newDirectFloatBuffer(vertexPositions)
    gl.glBufferData(GL.GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL.GL_STATIC_DRAW)
  }

  override def reshape(glAutoDrawable: GLAutoDrawable, i: Int, i1: Int, i2: Int, i3: Int): Unit = {}

  def createShaderProgram(): Int = {
    val gl = GLContext.getCurrentGL.asInstanceOf[GL4]
    val vShader = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER)
    val vShaderReader = new BufferedSource(classOf[PlainRedCubeFrame].getResourceAsStream("PlainRedCube_V.glsl"))
    val vShaderCode = vShaderReader.getLines().map(_ + '\n').toArray
    gl.glShaderSource(vShader, vShaderCode.length, vShaderCode, null, 0)
    gl.glCompileShader(vShader)
    checkShaderBuild(vShader)
    vShaderReader.close()

    val fShader = gl.glCreateShader(GL2ES2.GL_FRAGMENT_SHADER)
    val fShaderReader = new BufferedSource(classOf[PlainRedCubeFrame].getResourceAsStream("PlainRedCube_F.glsl"))
    val fShaderCode = fShaderReader.getLines().map(_ + '\n').toArray
    gl.glShaderSource(fShader, fShaderCode.length, fShaderCode, null, 0)
    gl.glCompileShader(fShader)
    checkShaderBuild(fShader)
    fShaderReader.close()

    val vfprogram = gl.glCreateProgram()
    gl.glAttachShader(vfprogram, vShader)
    gl.glAttachShader(vfprogram, fShader)
    gl.glLinkProgram(vfprogram)
    gl.glDeleteShader(vShader)
    gl.glDeleteShader(fShader)
    vfprogram
  }

  def perspective(fovy: Float, aspect: Float, n: Float, f: Float) = {
    val q = 1.0f / Math.tan(Math.toRadians(0.5f * fovy)).toFloat
    val A = q / aspect
    val B = (n + f) / (n - f)
    val C = (2.0f * n * f) / (n - f)

    val r = new Matrix3D()
    r.setElementAt(0, 0, A)
    r.setElementAt(1, 1, q)
    r.setElementAt(2, 2, B)
    r.setElementAt(3, 2, -1.0f)
    r.setElementAt(2, 3, C)
    r.setElementAt(3, 3, 0.0f)
    r
  }
}
