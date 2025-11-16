group = "net.kissenpvp"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation(project(":kissen-api"))

    implementation("com.zaxxer:HikariCP:7.0.2")

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