plugins {
    id("java-library")
//    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.4.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    implementation("org.reflections:reflections:0.10.2")
    compileOnly("net.luckperms:api:5.5")
    implementation(project(":api"))
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    shadowJar {
        archiveClassifier.set("") // remplace le jar normal
        relocate("org.reflections", "fr.jessee.firstSpawnRTP.libs.reflections")
    }

    build {
        dependsOn(shadowJar)
    }
    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
