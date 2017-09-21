package lwjgl

import org.lwjgl.Version
import org.lwjgl.opengl.WGL._
import org.lwjgl.glfw._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.lwjgl.system.MemoryStack._
import org.lwjgl.system.MemoryUtil._
import resource._

object Simple {
  var window:Long = _

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

  def initGL(): Unit ={
    glShadeModel(GL_SMOOTH)
    glClearColor(0.0f,0.0f,0.0f,0.5f)
    glClearDepth(1.0f)
    glEnable(GL_DEPTH_TEST)
    glDepthFunc(GL_LEQUAL)
    glHint(GL_PERSPECTIVE_CORRECTION_HINT,GL_NICEST)
  }

  def draw(): Unit = {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    glLoadIdentity()
  }

  def loop(): Unit = {
    GL.createCapabilities()
    initGL()

    while (!glfwWindowShouldClose(window)) {
      glfwSwapBuffers(window)
      glfwPollEvents()
    }
  }

  def resizeGLScene(width:Int, height:Int) ={
    glViewport(0,0,width,height)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
  }

  def run():Unit = {
    println(s"Hello LWJGL ${Version.getVersion} ! ")
    init()
    loop()

//    glfwFreeCallbacks(window)
    glfwDestroyWindow(window)

    glfwTerminate()
    glfwSetErrorCallback(null).free()

  }

  def main(args: Array[String]): Unit = {
    run()
  }
}
