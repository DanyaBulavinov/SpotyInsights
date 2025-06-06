plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.daniel.spotyinsights.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val properties = com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(project.rootDir)
        buildConfigField("String", "LASTFM_API_KEY", "\"${properties.getProperty("LASTFM_API_KEY", "")}\"")
        buildConfigField("String", "LASTFM_SHARED_SECRET", "\"${properties.getProperty("LASTFM_SHARED_SECRET", "")}\"")
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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":domain"))

    // AndroidX Core
    implementation(libs.core.ktx)

    // Dependency Injection
    implementation(libs.hilt.android)
//    kapt(libs.hilt.compiler)
    ksp(libs.hilt.compiler)

    // Network
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.moshi)

    // Database
    implementation(libs.bundles.room)
//    kapt(libs.room.compiler)
    ksp(libs.room.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.espresso.core)
}