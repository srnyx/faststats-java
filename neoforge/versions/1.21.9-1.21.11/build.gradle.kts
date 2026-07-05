extra.set("moduleName", "dev.faststats.neoforge.compat.v1_21_9")
extra.set("publishVersionSuffix", "mc1.21.9-1.21.11")

plugins {
    id("net.neoforged.moddev")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

tasks.compileJava {
    options.release.set(25)
}

neoForge {
    // version = "21.9.16-beta" // 1.21.9
    // version = "21.10.64" // 1.21.10
    version = "21.11.42" // 1.21.11
}

configurations.configureEach {
    resolutionStrategy.force("com.google.code.gson:gson:2.14.0")
}

dependencies {
    api(project(":neoforge"))
    api(project(":core"))
    implementation(project(":config"))
    compileOnly("net.neoforged:bus:8.0.5")
}
