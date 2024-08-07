plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.freshkeeper"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    namespace = "com.example.freshkeeper"
}

dependencies {
    implementation("com.google.android.material:material:1.9.0") // 중복 제거
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0") // 최신 안정 버전 사용
    // 기타 의존성은 그대로 유지합니다.
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // ML Kit Barcode Scanning dependency
    implementation("com.google.mlkit:barcode-scanning:17.0.3")

    // CameraX dependencies
    implementation("androidx.camera:camera-core:1.1.0")
    implementation("androidx.camera:camera-camera2:1.1.0")
    implementation("androidx.camera:camera-lifecycle:1.1.0")
    implementation("androidx.camera:camera-view:1.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0") // Kotlin 버전 일치
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
