plugins {
    java
    `maven-publish`
}

dependencies {
    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "net.kissenpvp"
            artifactId = "kissen-api"
            version = "1.0.0-SNAPSHOT"

            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
