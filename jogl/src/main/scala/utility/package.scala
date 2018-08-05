import com.jogamp.opengl.{GL2ES2, GL4, GLContext}

import scala.io.BufferedSource

package object utility {
  implicit def currentGL(): GL4 = {
    GLContext.getCurrentGL.asInstanceOf[GL4]
  }

  def checkShaderBuild(shader: Int) = {
    val gl = GLContext.getCurrentGL.asInstanceOf[GL4]
    val len = Array.ofDim[Int](1)
    val chWrittn = Array.ofDim[Int](1)

    gl.glGetShaderiv(shader, GL2ES2.GL_COMPILE_STATUS, len, 0)
    if (len(0) == 0) {
      gl.glGetShaderiv(shader,GL2ES2.GL_INFO_LOG_LENGTH,len,0)
      val log = Array.ofDim[Byte](len(0))
      gl.glGetShaderInfoLog(shader, len(0), chWrittn, 0, log, 0)
      val str = new String(log)
      throw new IllegalStateException(str)
    }
  }

  def checkProgramLink(program: Int) = {
    val gl = currentGL()
    val len = Array.ofDim[Int](1)
    val chWrittn = Array.ofDim[Int](1)

    gl.glGetProgramiv(program, GL2ES2.GL_LINK_STATUS, len, 0)
    if (len(0) == 0) {
      gl.glGetProgramiv(program,GL2ES2.GL_INFO_LOG_LENGTH,len,0)

      val log = Array.ofDim[Byte](len(0))
      gl.glGetProgramInfoLog(program, len(0), chWrittn, 0, log, 0)
      val str = new String(log)
      throw new IllegalStateException(str)
    }
  }

  def buildProgramAndLinkShaders(shaders:Int*):Int = {
    val gl = currentGL()
    val program = gl.glCreateProgram()
    shaders.foreach { shader ⇒
      gl.glAttachShader(program, shader)
    }
    gl.glLinkProgram(program)
    checkProgramLink(program)
    program
  }

  def buildShader(shader: Int, shaderFile: String) = {
    val gl = currentGL()
    val buffer = new BufferedSource(getClass.getClassLoader.getResourceAsStream(shaderFile))
    val sources = buffer.getLines().map(_ + '\n').toArray
    gl.glShaderSource(shader, sources.length, sources, null, 0)
    gl.glCompileShader(shader)

    checkShaderBuild(shader)
    shader
  }
}
