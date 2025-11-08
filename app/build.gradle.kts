// Archivo: app/build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // 1. SOLUCIÓN: Mueve el plugin de App Distribution aquí si lo necesitas.
    // Si no tienes 'firebase-appdistribution-gradle' en tu libs.versions.toml,
    // puedes añadirlo manualmente o usar la versión hardcodeada.
    // Ejemplo: id("com.google.firebase.appdistribution") version "5.0.0" apply false
    // Si ya está en tu toml como `libs.plugins.firebase.appdistribution`, úsalo:
    // alias(libs.plugins.firebase.appdistribution)
}

android {
    namespace = "com.example.proye_1003"
    compileSdk = 36

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
            // Si te vuelve a dar el error de DEPENDENCIES, descomenta la siguiente línea:
            // excludes += "META-INF/DEPENDENCIES"
        }
    }

    defaultConfig {
        applicationId = "com.example.proye_1003"
        minSdk = 24
        // 2. SOLUCIÓN: Actualiza targetSdk para que coincida con compileSdk
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Usar una única versión de Navigation Compose (consistente)
    implementation("androidx.navigation:navigation-compose:2.9.5")

    // Retrofit + OkHttp (una sola versión de cada)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Coil (una sola vez)
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Activity / Compose básica (usar BOM para versiones)
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Otras dependencias del catálogo (si ya las usas)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    //composer
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    //compose-material-dialogs
    implementation("io.github.vanpra.compose-material-dialogs:core:0.9.0")
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")

    implementation("androidx.compose.ui:ui-text:1.7.5")

    // NOTA: Se eliminaron duplicados como:
    // - implementación repetida de coil-compose
    // - varias versiones de retrofit/okhttp repetidas
    // - la dependencia "navigation-compose.jvmstubs" (jvmstubs causa duplicados con el runtime)
    // Si necesitas alguna dependencia que se haya eliminado, re-introducela con una sola versión consistente.

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
