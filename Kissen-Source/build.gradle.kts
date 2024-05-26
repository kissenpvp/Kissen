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
    testImplementation("org.slf4j:slf4j-api:2.0.1")
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
