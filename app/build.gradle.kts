plugins {
    id("com.android.application")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.ariasicuraprogetto"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ariasicuraprogetto"
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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Configurazione per la chiave API
    val localProperties = rootProject.file("local.properties")
    kotlinOptions {
        jvmTarget = "11"
    }
    if (localProperties.exists()) {
        val properties = mutableMapOf<String, String>()
        localProperties.inputStream().use { inputStream ->
            val lines = inputStream.bufferedReader().readLines()
            lines.forEach { line ->
                val trimmedLine = line.trim()
                if (trimmedLine.isNotEmpty() && trimmedLine.contains("=")) {
                    val (key, value) = trimmedLine.split("=", limit = 2)
                    properties[key.trim()] = value.trim()
                }
            }
        }

        val airVisualApiKey = properties["AIRVISUAL_API_KEY"] ?: ""

        buildTypes.forEach {
            it.buildConfigField("String", "AIRVISUAL_API_KEY", "\"$airVisualApiKey\"")
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.volley)
    implementation(libs.constraintlayout)
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Programmazione di rete, chiamata API per ottenere dati di rete

    // analisi dei dati
    implementation("com.google.code.gson:gson:2.8.9")
    // caricamento delle immagini
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // richiesta di rete
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Lombok
    compileOnly ("org.projectlombok:lombok:1.18.30")
    annotationProcessor ("org.projectlombok:lombok:1.18.30")
    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")

    // -------------------------------------------------------------
}