plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.komyut_v1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.komyut_v1"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.coordinatorlayout)
    implementation ("com.google.android.gms:play-services-maps:19.0.0")
    implementation (libs.androidx.constraintlayout)
    implementation (libs.material.v190)
    implementation(libs.compose.theme.adapter)
    implementation ("androidx.core:core-ktx:1.10.1")
    implementation(libs.play.services.location)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.filament.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}

