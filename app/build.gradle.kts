
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinSymbolProcessing)
}

android {
    namespace = "com.challenge.listadeciudades"
    compileSdk = 35

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md,LICENSE.txt,NOTICE.txt}"
        }
    }

    defaultConfig {
        applicationId = "com.challenge.listadeciudades"
        minSdk = 24
        targetSdk = 35
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Gson
    implementation(libs.google.gson)

    // OkHttp (descarga JSON)
    implementation(libs.squareup.okhttp)

    // Coroutines
    implementation(libs.jetbrains.kotlinx.coroutines.core)
    implementation(libs.jetbrains.kotlinx.coroutines.android)

    // Koin
    implementation(libs.insertkoin.koin.android)
    implementation(libs.insertkoin.koin.androidx.compose)

    // AndroidX Lifecycle (para ViewModel + StateFlow)
    implementation(libs.androidx.lifecycle.viewmodelKtx)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.google.maps.compose)
    implementation(libs.google.play.services.maps)

    implementation(libs.squareup.retrofit2.retrofit)
    implementation(libs.squareup.retrofit2.converter.gson)

    implementation(libs.coil.compose)

    // JUnit
    testImplementation(libs.junit)

// Kotlin Coroutines Test
    testImplementation(libs.kotlinx.coroutines.test)

// Mockito core y Kotlin support
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

// Assertions (opcional, pero recomendado)
    testImplementation(libs.hamcrest)

    testImplementation(libs.koin.test)
    testImplementation(libs.turbine)

    testImplementation(libs.mockk.core)
    androidTestImplementation(libs.mockk.android)
}
