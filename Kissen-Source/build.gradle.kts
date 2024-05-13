plugins {
    java
    `maven-publish`
}

dependencies {
    implementation(project(":kissen-api"))
    //adventure api
    compileOnly("net.kyori:adventure-text-serializer-gson:4.14.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.14.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")

    // database
    implementation("org.xerial:sqlite-jdbc:3.42.0.1")

    //mongodb
    implementation("org.mongodb:mongo-java-driver:3.12.10")

    //lombok
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("net.kyori:adventure-text-serializer-gson:4.14.0")
    testImplementation("net.kyori:adventure-text-serializer-legacy:4.14.0")
    testImplementation("net.kyori:adventure-text-minimessage:4.14.0")
    testImplementation("org.mockito:mockito-core:3.12.4")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "net.kissenpvp"
            artifactId = "kissen-source"
            version = "1.0.0-SNAPSHOT"

            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
