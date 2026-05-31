import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    `java-library`
    `maven-publish`
}

group = "net.kissenpvp"
version = "1.0.0-SNAPSHOT"

val adventureVersion = "4.20.0"

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
        maven("https://repo.kissenpvp.net/snapshots")
    }


    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
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

