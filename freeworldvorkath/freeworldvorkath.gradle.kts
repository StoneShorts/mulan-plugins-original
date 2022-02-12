version = "2.1.7"

project.extra["PluginName"] = "FWVorkath"
project.extra["PluginDescription"] = "Free World Vorkath"
project.extra["PluginProvider"] = "Mulan"
project.extra["PluginSupportUrl"] = "www.fuckyouanarchise.com"

dependencies {
    compileOnly(project(":autils"))
}

tasks {
    jar {
        manifest {
            attributes(
                mapOf(
                    "Plugin-Version" to project.version,
                    "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                    "Plugin-Provider" to project.extra["PluginProvider"],
                    "Plugin-Dependencies" to
                            arrayOf(
                                nameToId("AUtils")
                            ).joinToString(),
                    "Plugin-Description" to project.extra["PluginDescription"],
                    "Plugin-License" to project.extra["PluginLicense"]
                )
            )
        }
    }
}

