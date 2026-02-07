plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.skie)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Shared module for KDragonBall"
        homepage = "https://github.com/filipebatista/KDragonBall"
        version = "1.0"
        ios.deploymentTarget = "14.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
            // Export ViewModels to iOS
            export(libs.androidx.lifecycle.viewmodel)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            api(libs.androidx.lifecycle.viewmodel)
            implementation(libs.koin.core)
            implementation(libs.koin.core.viewmodel)

            // Arrow for functional programming
            api(libs.arrow.core)

            // Networking
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.core)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.example.kdragonball.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

skie {
    features {
        enableSwiftUIObservingPreview = true
    }
}
