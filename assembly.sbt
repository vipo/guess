import AssemblyKeys._ // put this at the top of the file

assemblySettings

assemblyOption in assembly ~= {
	_.copy(prependShellScript = Some(
		Seq(defaultShellScript.head, """exec java $JAVA_OPTS -jar "$0" "$@"""")
	))
}

jarName in assembly := { s"${name.value}.sh" }
