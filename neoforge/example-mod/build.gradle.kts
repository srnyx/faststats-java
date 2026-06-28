plugins {
    id("net.neoforged.moddev") version "2.0.141"
    kotlin("jvm")
}

neoForge {
    version = "26.1.2.76"
}

configurations.configureEach {
    resolutionStrategy.force("com.google.code.gson:gson:2.14.0")
}

dependencies {
    implementation(project(":neoforge:versions:26.1-26.2"))
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(project(":config").sourceSets["main"].output)
    from(project(":core").sourceSets["main"].output)
    from(project(":neoforge").sourceSets["main"].output)
    from(project(":neoforge:versions:26.1-26.2").sourceSets["main"].output)
}
