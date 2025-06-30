// build.gradle (Project-level)

plugins {
    // Aplicar plugins necesarios en los módulos, no a nivel global
    id("com.android.application") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
}

buildscript {
    repositories {
        google() // Este es necesario para Firebase y servicios de Google
        mavenCentral() // Este es un repositorio adicional
    }
    dependencies {
        // Versiones correctas de las herramientas de Gradle y Google Services
        classpath("com.android.tools.build:gradle:8.7.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.google.gms:google-services:4.3.15") // Plugin de Google Services
    }
}

allprojects {
    repositories {
        google() // Necesario para Firebase
        mavenCentral() // Repositorio adicional para otras dependencias
    }
}