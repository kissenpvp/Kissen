import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `java-library`
    `maven-publish`
}

group = "net.kissenpvp"
version = "1.0-SNAPSHOT"

val annotationsVersion = "26.0.2"
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
        val annotations = "org.jetbrains:annotations:$annotationsVersion"
        compileOnly(annotations)
        annotationProcessor(annotations)
        testCompileOnly(annotations)

        // SLF4j
        compileOnly("org.slf4j:slf4j-api:2.0.17")

        // adventure api
        compileOnly(platform("net.kyori:adventure-bom:$adventureVersion"))
        compileOnly("net.kyori:adventure-api")
        compileOnly("net.kyori:adventure-text-serializer-gson:4.22.0")

        // GSON
        compileOnly("com.google.code.gson:gson:2.13.1")

        // Testing
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
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

