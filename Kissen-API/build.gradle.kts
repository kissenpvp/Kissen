plugins {
    java
    `maven-publish`
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        groupId = "net.kissenpvp"
        artifactId = "kissen-core-api"
        version = "1.0.0-SNAPSHOT"
        from(components["java"])
    }
}


tasks.test {
    useJUnitPlatform()
}