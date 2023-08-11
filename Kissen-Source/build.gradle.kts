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

    //mongodb
    implementation("org.mongodb:mongo-java-driver:3.12.10")

    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}