package utility

import com.jogamp.opengl.{GL2ES2, GL4}

import scala.io.BufferedSource

class VertexShader(resName: String)(implicit gl: GL4) {
  val vShader = gl.glCreateShader(GL2ES2.GL_VERTEX_SHADER)
  val buffer = new BufferedSource(getClass.getResourceAsStream(resName))
  val codes = buffer.getLines().toArray
}
