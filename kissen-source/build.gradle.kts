group = "net.kissenpvp"
version = "1.0.0-SNAPSHOT"

val adventureVersion = "4.25.0"

dependencies {
    implementation(project(":kissen-api")) // depends on api

    implementation("com.zaxxer:HikariCP:7.0.2") // only for source

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Jar>("jar") {
    dependsOn(":kissen-api:jar")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}