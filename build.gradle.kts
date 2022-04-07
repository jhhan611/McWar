plugins {
    java
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.github.jhhan611"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("dev.jorel.CommandAPI:commandapi-core:6.4.0")
    //implementation("io.github.monun:kommand-api:2.6.6")
    compileOnly("io.papermc.paper:paper:1.17.1-R0.1-SNAPSHOT")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("MachangWars")
        archiveVersion.set("")
        //archiveFileName.set("C:\\path\\plugins\\MachangWars.jar") //output location
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "16"
        }
    }
}