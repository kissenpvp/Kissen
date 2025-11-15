import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `java-library`
    `maven-publish`
}

group = "net.kissenpvp"
version = "1.0-SNAPSHOT"

val adventureVersion = "4.20.0"

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
        maven("https://repo.kissenpvp.net/snapshots")
    }

    dependencies {
        // Annotations
        annotationProcessor("org.jspecify:jspecify:1.0.0")

        // Database
        implementation("org.flywaydb:flyway-core:11.11.0")
        implementation("org.flywaydb:flyway-mysql:11.11.0")

        // SLF4j
        compileOnly("org.slf4j:slf4j-api:2.0.17")

        // AdventureAPI
        compileOnly(platform("net.kyori:adventure-bom:$adventureVersion"))
        compileOnly("net.kyori:adventure-api")
        compileOnly("net.kyori:adventure-text-serializer-gson:4.22.0")

        // GSON
        compileOnly("com.google.code.gson:gson:2.13.1")

        // Preconditions
        compileOnly("com.google.guava:guava:33.5.0-jre")

        // Testing
        testImplementation(platform("org.junit:junit-bom:5.13.4"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }

    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

    publishing {
        repositories {
            maven("https://repo.kissenpvp.net/snapshots") {
                name = "kissen"
                credentials(PasswordCredentials::class)
            }
        }

        publications.create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}

