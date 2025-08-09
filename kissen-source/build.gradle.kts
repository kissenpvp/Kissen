group = "net.kissenpvp"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(project(":kissen-api"))

    implementation("org.flywaydb:flyway-core:11.11.0")
    implementation("org.flywaydb:flyway-mysql:11.11.0")

    testImplementation("com.mysql:mysql-connector-j:9.2.0")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}