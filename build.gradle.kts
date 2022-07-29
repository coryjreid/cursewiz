plugins {
    id("java")
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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}