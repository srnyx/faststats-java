plugins {
    id("net.fabricmc.fabric-loom")
    kotlin("jvm")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

tasks.compileJava {
    options.release.set(25)
}

dependencies {
    implementation(project(":fabric:versions:26.1"))
    minecraft("com.mojang:minecraft:26.1.2")
    implementation("net.fabricmc.fabric-api:fabric-api:0.150.0+26.1.2")
    compileOnly("net.fabricmc:fabric-loader:0.19.3")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(project(":config").sourceSets["main"].output)
    from(project(":core").sourceSets["main"].output)
    from(project(":fabric").sourceSets["main"].output)
    from(project(":fabric:versions:26.1").sourceSets["main"].output)
}
