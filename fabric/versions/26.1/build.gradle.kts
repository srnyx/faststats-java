extra.set("moduleName", "dev.faststats.fabric.compat.v26_1")
extra.set("publishVersionSuffix", "mc26.1-26.2")

plugins {
    id("net.fabricmc.fabric-loom")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

tasks.compileJava {
    options.release.set(25)
}

dependencies {
    api(project(":fabric"))
    api(project(":core"))
    implementation(project(":config"))
    minecraft("com.mojang:minecraft:26.1.2")
    compileOnly("net.fabricmc.fabric-api:fabric-api:0.150.0+26.1.2")
    compileOnly("net.fabricmc:fabric-loader:0.19.3")
}
