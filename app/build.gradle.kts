plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.proye_1003"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.proye_1003"
        minSdk = 24
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
    kotlinOptions { jvmTarget = "11" }

    buildFeatures { compose = true }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
            // Si vuelve a salir, descomenta:
            // excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    // ---------- Compose: usa BOM para alinear versiones ----------
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)        // <- Material 3 (TopAppBar, etc.)
    implementation("androidx.activity:activity-compose:1.9.3")

    // Navegación Compose (una sola versión coherente)
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // ViewModel + coroutines en Compose
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Retrofit + Gson + OkHttp (versiones actuales y consistentes)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Imágenes en Compose
    implementation("io.coil-kt:coil-compose:2.6.0")

    // AndroidX básicos (si tu proyecto los usa)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // *Opcional* si aún tienes pantallas XML:
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.compose.animation.core.lint)

    // Material (legacy) NO es necesario para Material3; puedes quitarlo si no lo usas:
    // implementation(libs.material)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
