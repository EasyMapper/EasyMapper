plugins {
    java
    id("io.freefair.lombok") version "8.6"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    testImplementation(project(":easymapper"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testImplementation("io.github.autoparams:autoparams:10.1.0")
    testImplementation("io.github.autoparams:autoparams-mockito:10.1.0")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("javax.persistence:javax.persistence-api:2.2")
}

tasks.test {
    useJUnitPlatform()
}
