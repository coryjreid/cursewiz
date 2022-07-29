import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.configurationcache.extensions.capitalized

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.coryjreid"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.erdbeerbaerlp.de/repository/maven-public/")
    }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
    implementation("com.martiansoftware:jsap:2.1")
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("de.erdbeerbaerlp:cfcore:1.0.0")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha16")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha7")
    implementation("com.google.guava:guava:31.1-jre")
    constraints {
        // Guava uses Jackson but we want to control the version
        implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to project.group.toString() + "." + project.name + "." + project.name.capitalized()
            )
        )
    }

    archiveClassifier.set("")
    archiveBaseName.set(project.name.capitalized())
    archiveVersion.set(archiveVersion.get().replace("-SNAPSHOT", ""))
}
