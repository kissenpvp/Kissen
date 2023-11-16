plugins {
    java
    `maven-publish`
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("net.kyori:adventure-text-minimessage:4.14.0")
}

tasks.test {
    useJUnitPlatform()
}
