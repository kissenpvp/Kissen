import org.ajoberstar.grgit.Grgit
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    id("org.ajoberstar.grgit") version "5.2.0"
}


allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
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
        compileOnly("org.jetbrains:annotations:24.0.0")
        compileOnly("net.kyori:adventure-api:4.13.0")
        compileOnly("com.google.code.gson:gson:2.10.1")
        implementation("io.netty:netty-all:4.1.87.Final")
    }
}

tasks.register<DefaultTask>("cloneVelocity") {
    doLast {
        val velocityDirectory = File("${project.rootDir}/KissenVelocity")
        if(!velocityDirectory.exists())
        {
            Grgit.clone {
                dir = velocityDirectory
                uri = "https://github.com/KissenPvP/KissenVelocity.git"
            }
        }
    }
}

tasks.register("applyPatches")
tasks.named("applyPatches") {
    dependsOn("cloneVelocity")
}