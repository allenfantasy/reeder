name := "my-first-application"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.json" % "org.json" % "chargebee-1.0",
  "commons-io" % "commons-io" % "2.4"
)     

play.Project.playJavaSettings