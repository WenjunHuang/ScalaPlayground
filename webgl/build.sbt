enablePlugins(ScalaJSPlugin, WorkbenchPlugin)

name := "webgl"

version := "1.0"

organization := "wenjun huang"

val bindingVersion = "11.0.1"

//scalaJSUseMainModuleInitializer := true
skip in packageJSDependencies := false
//jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
libraryDependencies ++= {
  Seq(
    "com.thoughtworks.binding" %%% "binding" % bindingVersion,
    "com.thoughtworks.binding" %%% "dom" % bindingVersion,
    "com.thoughtworks.binding" %%% "route" % bindingVersion
  )
}

workbenchStartMode := WorkbenchStartModes.Manual

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
