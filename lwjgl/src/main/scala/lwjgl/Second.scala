package lwjgl

import org.apache.commons.io.IOUtils
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl._
import org.lwjgl.system.MemoryStack.stackPush
import resource.managed

object Second {
  def main(args: Array[String]): Unit = {
    if (!glfwInit())
      throw new IllegalStateException("Unable to initialize GLFW")
    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
    glfwWindowHint(GLFW_SAMPLES, 4)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

    val window = glfwCreateWindow(
      1024,
      768,
      "Tutorial 01",
      0, 0)
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

//    glGenVertexArrays()
    glfwMakeContextCurrent(window)
    glfwSetInputMode(window, GLFW_STICKY_KEYS, GL_TRUE)

    GL.createCapabilities()
    val vertices:Array[Float] = Array(
      0.5f, 0.5f, 0.0f,1.0f,0.0f,0.0f,
      0.5f, -0.5f, 0.0f,0.0f,1.0f,0.0f,
      -0.5f,-0.5f,0.0f,0.0f,0.0f,1.0f
    )

    val texCoords = Array(
      0.0f,0.0f,
      1.0f,0.0f,
      0.5f,1.0f
    )

    val vao = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    val vbo = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,vbo)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER,vertices,GL15.GL_STATIC_DRAW)

    val shaderProgram = new Shader("vertex.glsl","fragment.glsl")

    GL20.glVertexAttribPointer(0,3,GL11.GL_FLOAT,false,24,0)
    GL20.glEnableVertexAttribArray(0)
    glVertexAttribPointer(1,3,GL_FLOAT,false,24,12)
    glEnableVertexAttribArray(1)


    while (!glfwWindowShouldClose(window)) {
      shaderProgram.use()
      shaderProgram.setFloat("xOffset",0.0f)

      GL30.glBindVertexArray(vao)
      GL11.glDrawArrays(GL11.GL_TRIANGLES,0,GL11.GL_UNSIGNED_INT)

      glfwSwapBuffers(window)
      glfwPollEvents()
    }
  }
}
