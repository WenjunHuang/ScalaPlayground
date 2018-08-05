package lwjgl

import org.apache.commons.io.IOUtils
import org.lwjgl.opengl.GL20._

class Shader(vertexPath:String,fragmentPath:String) {
  val vertexCode = IOUtils.toString(getClass.getClassLoader.getResource(vertexPath),"UTF-8")
  val fragmentCode = IOUtils.toString(getClass.getClassLoader.getResource(fragmentPath),"UTF-8")

  val vertex = glCreateShader(GL_VERTEX_SHADER)
  glShaderSource(vertex,vertexCode)
  glCompileShader(vertex)

  val result = Array(0)
  glGetShaderiv(vertex,GL_COMPILE_STATUS,result)
  if (result(0) == 0) {
    val err = glGetShaderInfoLog(vertex)
    println(err)
    throw new Exception(err)
  }

  val fragment = glCreateShader(GL_FRAGMENT_SHADER)
  glShaderSource(fragment,fragmentCode)
  glCompileShader(fragment)
  glGetShaderiv(fragment,GL_COMPILE_STATUS,result)
  if (result(0)==0){
    val err = glGetShaderInfoLog(fragment)
    println(err)
    throw new Exception(err)
  }

  val program = glCreateProgram()
  glAttachShader(program,vertex)
  glAttachShader(program,fragment)
  glLinkProgram(program)
  glGetProgramiv(program,GL_LINK_STATUS,result)
  if (result(0)==0){
    val err = glGetProgramInfoLog(program)
    throw new Exception(err)
  }

  glDeleteShader(vertex)
  glDeleteShader(fragment)

  def use(): Unit ={
    glUseProgram(program)
  }

  def setBool(name:String,value:Boolean):Unit = {
    glUniform1i(glGetUniformLocation(program,name),if (value) 1 else 0)
  }
  def setInt(name:String,value:Int):Unit={
    glUniform1i(glGetUniformLocation(program,name),value)
  }
  def setFloat(name:String,value:Float):Unit ={
    glUniform1f(glGetUniformLocation(program,name),value)
  }
}
