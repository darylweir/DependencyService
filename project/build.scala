import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.earldouglas.xwp.JettyPlugin
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

object DependencyServiceBuild extends Build {
  val Organization = "com.darylweir"
  val Name = "DependencyService"
  val Version = "0.1"
  val ScalaVersion = "2.11.8"
  val ScalatraVersion = "2.4.0.RC1"

  lazy val project = Project (
    "depservice",
    file("."),
    settings = seq(com.typesafe.sbt.SbtStartScript.startScriptForClassesSettings: _*) ++ ScalatraPlugin.scalatraSettings ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
      resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases",
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "org.scalatra" %% "scalatra-json" % "2.3.0",
        "org.json4s"   %% "json4s-jackson" % "3.2.9",
        "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
        "org.mortbay.jetty" % "jetty" % "6.1.22" % "container",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.7.v20120910" % "compile",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "compile;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar")),
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "compile;container;provided",
        "org.scala-saddle" %% "saddle-core" % "1.3.+",
        "org.scalactic" %% "scalactic" % "3.0.0",
        "org.scalatest" %% "scalatest" % "3.0.0" % "test"
      ),
      dependencyOverrides := Set(
      "org.scala-lang" % "scala-library" % ScalaVersion,
      "org.scala-lang" % "scala-reflect" % ScalaVersion,
      "org.scala-lang" % "scala-compiler" % ScalaVersion
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
            ),  /* add extra bindings here */
            Some("templates")
          )
        )
      }
    )
  ).enablePlugins(JettyPlugin)
}
