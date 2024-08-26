plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt") // Kotlin Annotation Processing Tool (kapt) 추가
    id("com.google.gms.google-services") // Firebase 및 Google 서비스 플러그인 적용
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

        // API_KEY 설정
        buildConfigField("String", "API_KEY", "\"AIzaSyCeweSgLmWGyZqMRzTSRRdV1dzAHMy8R2w\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        allWarningsAsErrors = true // 모든 경고를 오류로 처리
        // 이 부분을 제거합니다:
        // freeCompilerArgs += listOf("-Xlint:deprecation")
        // Deprecated API 사용에 대한 경고를 표시할 필요가 없으므로 이 줄을 삭제했습니다.
    }

    namespace = "com.example.freshkeeper"
}

dependencies {
    implementation("com.google.android.material:material:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.0")
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

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Glide 라이브러리 및 kapt 사용
    implementation("com.github.bumptech.glide:glide:4.13.2")
    kapt("com.github.bumptech.glide:compiler:4.13.2")
}
