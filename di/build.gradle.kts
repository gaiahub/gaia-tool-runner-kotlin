plugins {
    kotlin("jvm")
}

group = "ai.gaiahuhb.runner"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.koin.core)
    implementation(libs.ktorm.core)
    implementation(libs.ktorm.support.postgresql)
    implementation(project(":data"))
    implementation(project(":domain"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}