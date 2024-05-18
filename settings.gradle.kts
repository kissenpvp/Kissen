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

val projects = listOf("Kissen-API", "Kissen-Source", "KissenPaper")
val paperProjects = listOf("KissenPaper-API", "KissenPaper-Server", "paper-api-generator")

projects.forEach { name ->
    val projName = name.lowercase(Locale.ENGLISH)
    include(projName)
    findProject(":$projName")?.projectDir = file(name)
}

paperProjects.forEach { name ->
    val projName = name.lowercase(Locale.ENGLISH)
    include("kissenpaper:$projName")
    findProject(":kissenpaper:$projName")?.projectDir = file("KissenPaper/$name")
}

optionalInclude("test-plugin")

fun optionalInclude(name: String, op: (ProjectDescriptor.() -> Unit)? = null) {
    val settingsFile = file("KissenPaper/$name.settings.gradle.kts")
    if (settingsFile.exists()) {
        apply(from = settingsFile)
        findProject(":kissenpaper:$name")?.let { op?.invoke(it) }
    } else {
        settingsFile.writeText(
            """
            // Uncomment to enable the '$name' project
            // include(":$name")

            """.trimIndent()
        )
    }
}
