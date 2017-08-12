name := "gsw"

scalaVersion:= "2.12.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % "test"
libraryDependencies += "org.eclipse.jgit" % "org.eclipse.jgit.http.server" % "4.8.0.201706111038-r"
libraryDependencies += "org.eclipse.jetty" % "jetty-servlet" % "9.4.6.v20170531"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"
libraryDependencies += "com.typesafe" % "config" % "1.3.1"
libraryDependencies += "org.scalatra" %% "scalatra" % "2.5.1"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.3"
libraryDependencies += "org.apache.httpcomponents" % "fluent-hc" % "4.5.3"
libraryDependencies += "com.vladsch.flexmark" % "flexmark" % "0.26.2"

cancelable in Global := true

/**
  * Using this over <JavaServerAppPackaging> because that produces DEB/RPM, this requiring dependency on the host OS.
  */
enablePlugins(JavaAppPackaging)
enablePlugins(GitVersioning)
git.useGitDescribe := true
packageName in Universal := packageName.value