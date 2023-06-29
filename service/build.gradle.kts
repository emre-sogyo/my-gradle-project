plugins {
    id("java-conventions")
}

group = "nl.sogyo.myproject"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(project(mapOf("path" to ":domain")))
}

tasks.test {
    useJUnitPlatform()
}