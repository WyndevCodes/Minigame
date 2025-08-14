plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "me.wyndev"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("net.minestom:minestom:2025.07.30-1.21.8")
    implementation("net.kyori:adventure-text-minimessage:4.24.0")
    implementation("org.apache.commons:commons-text:1.14.0")
    implementation("com.github.TogAr2:MinestomPvP:56a831b41c")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "me.wyndev.minigame.Main"
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("") // Prevent the -all suffix on the shadowjar file.
    }
}