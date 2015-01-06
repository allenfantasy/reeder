name := "my-first-application"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.json" % "org.json" % "chargebee-1.0",
  "commons-io" % "commons-io" % "2.4",
  "com.nimbusds" % "nimbus-jose-jwt" % "3.5",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
)     

play.Project.playJavaSettings
