import java.util.Locale

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

if (!file(".git").exists()) {
    val errorText = """

        =====================[ ERROR ]=====================
         The Kissen project directory is not a properly cloned Git repository.
        ===================================================
    """.trimIndent()
    error(errorText)
}

rootProject.name = "kissen"

val projects = listOf("Kissen-API", "Kissen-Source", "Pulvinar")
val paperProjects = listOf("Pulvinar-API", "Pulvinar-Server", "paper-api-generator")

projects.forEach { name ->
    val projName = name.lowercase(Locale.ENGLISH)
    include(projName)
    findProject(":$projName")?.projectDir = file(name)
}

paperProjects.forEach { name ->
    val projName = name.lowercase(Locale.ENGLISH)
    include("pulvinar:$projName")
    findProject(":pulvinar:$projName")?.projectDir = file("Pulvinar/$name")
}

optionalInclude("test-plugin")

fun optionalInclude(name: String, op: (ProjectDescriptor.() -> Unit)? = null) {
    val settingsFile = file("Pulvinar/$name.settings.gradle.kts")
    if (settingsFile.exists()) {
        apply(from = settingsFile)
        findProject(":pulvinar:$name")?.let { op?.invoke(it) }
    } else {
        settingsFile.writeText(
            """
            // Uncomment to enable the '$name' project
            // include(":$name")

            """.trimIndent()
        )
    }
}
