name := "akka"

version := "1.0"

organization := "wenjun huang"

val lwjglVersion = "3.1.2"
val lwjglNatives = sys.props.get("os.name") match {
  case Some("Mac OS X") ⇒ "natives-macos"
  case Some("Windows") ⇒ "natives-windows"
}

libraryDependencies ++= Seq(
  "commons-io"%"commons-io" %"2.5",
  "org.lwjgl" % "lwjgl" % lwjglVersion,
  "org.lwjgl" % "lwjgl-glfw" % lwjglVersion,
  "org.lwjgl" % "lwjgl-opengl" % lwjglVersion,
  "org.lwjgl" % "lwjgl-jemalloc" % lwjglVersion,
  "org.lwjgl" % "lwjgl-stb" % lwjglVersion,
  "org.lwjgl" % "lwjgl" % lwjglVersion classifier lwjglNatives,
  "org.lwjgl" % "lwjgl-glfw" % lwjglVersion classifier lwjglNatives,
  "org.lwjgl" % "lwjgl-jemalloc" % lwjglVersion classifier lwjglNatives,
  "org.lwjgl" % "lwjgl-openal" % lwjglVersion classifier lwjglNatives,
  "org.lwjgl" % "lwjgl-opengl" % lwjglVersion classifier lwjglNatives,
  "org.lwjgl" % "lwjgl-stb" % lwjglVersion classifier lwjglNatives,
  "com.jsuereth" %% "scala-arm" % "2.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

