import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.skie)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.buildKonfig)
}

kotlin {
    androidTarget {
        // Configure JVM target and language version
        compilations.all {
            compileTaskProvider.configure {
                kotlinOptions {
                    jvmTarget = "11"
                    apiVersion = "2.2"
                    languageVersion = "2.2"
                    freeCompilerArgs += "-Xexpect-actual-classes"
                }
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.compilations.all {
            compileTaskProvider.configure {
                kotlinOptions {
                    apiVersion = "2.2"
                    languageVersion = "2.2"
                    freeCompilerArgs += "-Xexpect-actual-classes"
                }
            }
        }
        iosTarget.binaries.framework {
            export(libs.androidx.lifecycle.viewmodel)
            baseName = "ComposeApp"
            isStatic = true
            export("io.github.sunildhiman90:kmauth-supabase:0.3.1")
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
            implementation(libs.android.driver)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.okhttp)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.android.material)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.runtime)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
            implementation(libs.koin.androidx.compose)
            implementation(libs.coil.compose)
            api(libs.androidx.lifecycle.viewmodel)
            api(compose.foundation)
            api(compose.animation)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.material.icons.extended)
            implementation(libs.coil.network.ktor)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.kmauth.google.compose)
            implementation(libs.kmauth.google)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            api("io.github.sunildhiman90:kmauth-supabase:0.3.1")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.ios)
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }
}

android {
    namespace = "com.shortspark.emaliestates"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.shortspark.emaliestates"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // REMOVED: kotlinOptions { ... } block from here.
    // It is now handled in the kotlin { androidTarget { ... } } block above.

    buildFeatures {
        buildConfig = true
    }
}

buildkonfig {
    packageName = "com.shortspark.emaliestates"

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { localProperties.load(it) }
    }

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "WEB_CLIENT_ID", localProperties.getProperty("webClientId") ?: "")
        buildConfigField(FieldSpec.Type.STRING, "WEB_CLIENT_SECRET", localProperties.getProperty("webClientSecret") ?: "")
        buildConfigField(FieldSpec.Type.STRING, "SUPABASE_URL", localProperties.getProperty("supabaseUrl") ?: "")
        buildConfigField(FieldSpec.Type.STRING, "SUPABASE_KEY", localProperties.getProperty("supabaseKey") ?: "")
        buildConfigField(FieldSpec.Type.STRING, "ALL_PROPERTIES_ENDPOINT", localProperties.getProperty("apiEndpoint") ?: "")
    }
}

sqldelight {
    databases {
        create("PropertyDatabase") {
            packageName.set("com.shortspark.emaliestates.database")
        }
    }
}