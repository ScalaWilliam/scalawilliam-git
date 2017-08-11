scalaVersion:= "2.12.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % "test"
libraryDependencies += "org.eclipse.jgit" % "org.eclipse.jgit.http.server" % "4.8.0.201706111038-r"
libraryDependencies += "org.eclipse.jetty" % "jetty-servlet" % "9.4.6.v20170531"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"
libraryDependencies += "com.typesafe" % "config" % "1.3.1"

cancelable in Global := true
