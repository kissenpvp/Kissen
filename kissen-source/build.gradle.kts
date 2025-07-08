group = "net.kissenpvp"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(project(":kissen-api"))
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    manifest {
        attributes["Main-Class"] = "your.main.Class" // If you have a main class
    }
}