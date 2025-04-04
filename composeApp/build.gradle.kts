import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    kotlin("plugin.serialization") version "1.9.22"
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_18)
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.androidx.media3.exoplayer)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

//            implementation(libs.room.runtime)
//            runtimeOnly("androidx.sqlite:sqlite-bundled:2.5.0-alpha13")
            implementation(libs.navigation.compose)
            implementation(libs.koin.core)
            //implementation(libs.datastore)
            //implementation(libs.datastore.preferences)
            implementation(libs.kotlinx.serialization.json)
//            implementation(libs.androidx.ui.tooling)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.mpfilepicker)
            implementation(libs.mp3agic)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            implementation(libs.discord.rpc)
            implementation(libs.lavaplayer)
            implementation("org.slf4j:slf4j-api:2.0.7")
            implementation("org.slf4j:slf4j-simple:2.0.7")
//            implementation("dev.lavalink.youtube:common:1.11.4")
            implementation(libs.mediaplayer.kmp)
        }
    }
}

android {
    namespace = "chu.monscout.kagamin"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "chu.monscout.kagamin"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 7
        versionName = "1.0.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        javaHome = System.getenv("JDK_21")

        mainClass = "chu.monscout.kagamin.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Rpm)//, TargetFormat.Dmg, )
            packageName = "Kagamin"
            packageVersion = "1.0.6"

            modules("java.compiler", "java.instrument", "java.naming", "java.scripting", "java.security.jgss", "java.sql", "jdk.management", "jdk.unsupported")

            buildTypes.release.proguard {
//                configurationFiles.from(project.file("compose-desktop.pro"))
                isEnabled = false
            }

            linux {
                ///home/neeko/Android/jdk-17.0.14+7
//                javaHome = "/home/neeko/Android/java-21-openjdk" //System.getenv("JDK_17")
                iconFile.set(project.file("kagamin.png"))
                shortcut = true
            }

            windows {
                iconFile.set(project.file("kagamin.ico"))
                shortcut = true
            }
        }
    }
}
