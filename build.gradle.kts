plugins {
    id("java")
}

// Cette section s'applique à TOUS les sous-dossiers (api et core)
subprojects {
    apply(plugin = "java")

    group = "fr.jessee.firstSpawnRTP" // Remplace par ton vrai package
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}

//plugins {
//    id("java-library")
//    id("com.gradleup.shadow") version "9.4.2"
//}
//
//subprojects {
//    apply(plugin = "java-library")
//    apply(plugin = "com.gradleup.shadow")
//}
//
//repositories {
//    mavenCentral()
//    maven("https://repo.papermc.io/repository/maven-public/")
//}
//
//dependencies {
//    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
//    implementation("org.reflections:reflections:0.10.2")
//    compileOnly("net.luckperms:api:5.5")
//}
//
//java {
//    toolchain.languageVersion = JavaLanguageVersion.of(25)
//}
//
//tasks {
//    shadowJar {
//        archiveClassifier.set("") // remplace le jar normal
//        relocate("org.reflections", "fr.jessee.firstSpawnRTP.libs.reflections")
//    }
//
//    build {
//        dependsOn(shadowJar)
//    }
//    processResources {
//        val props = mapOf("version" to version)
//        filesMatching("plugin.yml") {
//            expand(props)
//        }
//    }
//}
