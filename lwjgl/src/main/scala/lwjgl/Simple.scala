package lwjgl

import lwjgl.Second.getClass
import org.apache.commons.io.IOUtils
import org.lwjgl.Version
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw._
import org.lwjgl.opengl.{GL, GL15, GL20}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.system.MemoryStack._
import resource._

object Simple {
  var window: Long = _
  var renderingProgram: Int = -1
  var vao = Array[Int](0)

  def init(): Unit = {
    GLFWErrorCallback.createPrint(System.err).set()
    if (!glfwInit())
      throw new IllegalStateException("Unable to initialize GLFW")

    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)


    window = glfwCreateWindow(300, 300, "Hello World!", 0, 0)
    if (window == 0)
      throw new RuntimeException("Failed to create the GLFW window")

    glfwSetKeyCallback(window, { (window, key, scancode, action, mods) ⇒
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
        glfwSetWindowShouldClose(window, true)
    })

    for {stack ← managed(stackPush())} {
      val pWidth = stack.mallocInt(1)
      val pHeight = stack.mallocInt(1)

      glfwGetWindowSize(window, pWidth, pHeight)
      val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

      glfwSetWindowPos(
        window,
        (vidmode.width() - pWidth.get(0)) / 2,
        (vidmode.height() - pHeight.get(0)) / 2
      )

      glfwMakeContextCurrent(window)
      glfwSwapInterval(1)
      glfwShowWindow(window)
    }
  }

  def initGL(): Unit = {
    //    glShadeModel(GL_SMOOTH)
    val vertices: Array[Float] = Array(
      -0.5f, -0.5f, 0.0f,
      0.5f, -0.5f, 0.0f,
      0.0f, 0.5f, 0.0f
    )
    val vbo = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW)

    val vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER)

    val s = IOUtils.toString(getClass.getClassLoader.getResourceAsStream("vertex.glsl"),
                             "UTF-8")
    GL20.glShaderSource(vertexShader, s)
    GL20.glCompileShader(vertexShader)
  }

  def createShaderProgram(): Int = {
    val vshaderSource =
      """
        |#version 430
        |void main(void) {
        |gl_Position = vec4(0.0,0.0,0.0,1.0);
        |}
      """.stripMargin

    val fshaderSource =
      """
        |#version 430
        |out vec4 color;
        |void main(void) {
        |color = vec4(0.0,0.0,1.0,1.0);
        |}
      """.stripMargin

    val vShader = glCreateShader(GL_VERTEX_SHADER)
    glShaderSource(vShader, vshaderSource)
    glCompileShader(vShader)

    val fShader = glCreateShader(GL_FRAGMENT_SHADER)
    glShaderSource(fShader, fshaderSource)
    glCompileShader(fShader)

    val vfprogram = glCreateProgram()
    glAttachShader(vfprogram, vShader)
    glAttachShader(vfprogram, fShader)
    glLinkProgram(vfprogram)

    glDeleteShader(vShader)
    glDeleteShader(fShader)
    vfprogram
  }

  def draw(): Unit = {
    glPointSize(30.0f)
    glUseProgram(renderingProgram)
    glDrawArrays(GL_POINTS, 0, 1)
  }

  def loop(): Unit = {
    GL.createCapabilities()
    initGL()

    while (!glfwWindowShouldClose(window)) {
      draw()
      glfwSwapBuffers(window)
      glfwPollEvents()
    }
  }

  def run(): Unit = {
    println(s"Hello LWJGL ${Version.getVersion} ! ")
    init()
    loop()

    glfwDestroyWindow(window)
    glfwTerminate()
    glfwSetErrorCallback(null).free()
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}
