group = "net.kissenpvp"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(project(":kissen-api"))
    implementation("com.zaxxer:HikariCP:7.0.2")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}