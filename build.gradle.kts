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
    implementation("de.erdbeerbaerlp:cfcore:1.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}