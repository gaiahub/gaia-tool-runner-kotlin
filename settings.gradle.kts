
dependencyResolutionManagement {
    versionCatalogs {
        create("libs2") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}



rootProject.name = "gaia-tool-runner-kotlin"
include("data")
include("app")
include("di")
include("build-logic")
include("domain")
