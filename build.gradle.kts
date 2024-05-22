import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    `maven-publish`
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    publishing {
        repositories {
            maven("https://repo.kissenpvp.net/repository/maven-snapshots/") {
                name = "kissenpvp"
                credentials(PasswordCredentials::class)
            }
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }

    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test> {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

    dependencies {
        compileOnly("org.slf4j:slf4j-api:2.0.1")
        compileOnly("com.google.code.gson:gson:2.10.1")
        implementation("io.netty:netty-codec-haproxy:4.1.97.Final")

        testCompileOnly("org.jetbrains:annotations:24.0.0")
    }
}

tasks.register("printMinecraftVersion") {
    doLast {
        println(providers.gradleProperty("mcVersion").get().trim())
    }
}

tasks.register("printPulvinarVersion") {
    doLast {
        println(project.version)
    }
}
