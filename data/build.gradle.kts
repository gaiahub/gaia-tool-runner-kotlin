plugins {
    kotlin("jvm")
}

group = "ai.gaiahuhb.runner"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktorm.core)
    implementation(libs.ktorm.support.postgresql)
    implementation(libs.koin.core)
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}