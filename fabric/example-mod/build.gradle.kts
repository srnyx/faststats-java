plugins {
    id("fabric-loom")
}

dependencies {
    implementation(project(":fabric"))
    mappings(loom.officialMojangMappings())
    minecraft("com.mojang:minecraft:1.21.11")
    modCompileOnly("net.fabricmc:fabric-loader:0.19.3")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(project(":config").sourceSets["main"].output)
    from(project(":core").sourceSets["main"].output)
    from(project(":fabric").sourceSets["main"].output)
}