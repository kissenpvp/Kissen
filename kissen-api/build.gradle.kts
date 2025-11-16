group = "net.kissenpvp"
version = "1.0.0-SNAPSHOT"

val adventureVersion = "4.25.0"

dependencies {
    annotationProcessor("org.jspecify:jspecify:1.0.0")

    api("org.flywaydb:flyway-core:11.11.0")
    api("org.flywaydb:flyway-mysql:11.11.0")

    compileOnlyApi("org.slf4j:slf4j-api:2.0.17")
    compileOnlyApi("com.google.guava:guava:33.5.0-jre")

    compileOnlyApi(platform("net.kyori:adventure-bom:$adventureVersion"))
    compileOnlyApi("net.kyori:adventure-api")
    compileOnlyApi("net.kyori:adventure-text-serializer-gson:4.22.0")

    compileOnlyApi("com.google.code.gson:gson:2.13.1")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}