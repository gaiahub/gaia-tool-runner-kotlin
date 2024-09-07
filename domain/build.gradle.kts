plugins {
    kotlin("jvm")
}

group = "ai.gaiahuhb.runner"
version = "0.0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.koin.core)
    implementation(project(":data"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}