plugins {
    id("com.gradleup.shadow")
    kotlin("jvm")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

tasks.compileJava {
    options.release.set(25)
}

repositories {
    maven("https://maven.hytale.com/pre-release")
}

dependencies {
    compileOnly("com.hypixel.hytale:Server:2026.05.07-5efa15f6d")
    implementation(project(":hytale"))
}

tasks.shadowJar {
    // optionally relocate faststats
    relocate("dev.faststats", "com.example.utils.faststats")
}
