plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.ariasicura"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ariasicura"
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
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}