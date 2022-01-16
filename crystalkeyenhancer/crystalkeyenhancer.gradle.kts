

version = "0.0.1"

project.extra["PluginName"] = "Crystal Key Enhancer"
project.extra["PluginDescription"] = "Enhances your crystal keys in Prif."

dependencies {
    compileOnly(project(":autils"))
}

tasks {
    jar {
        manifest {
            attributes(mapOf(
                "Plugin-Version" to project.version,
                "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                "Plugin-Provider" to project.extra["PluginProvider"],
                "Plugin-Dependencies" to
                        arrayOf(
                            nameToId("AUtils")).joinToString(),
                "Plugin-Description" to project.extra["PluginDescription"],
                "Plugin-License" to project.extra["PluginLicense"]
            ))
        }
    }
}