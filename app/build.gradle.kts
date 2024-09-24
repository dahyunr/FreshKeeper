plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt") // Kotlin Annotation Processing Tool 추가
    // Firebase 및 Google 서비스 플러그인 제거
    // id("com.google.gms.google-services")
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
        vectorDrawables.useSupportLibrary = true
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

    // ML Kit Barcode Scanning 의존성 추가
    implementation("com.google.mlkit:barcode-scanning:17.0.3")

    // CameraX 의존성 추가
    implementation("androidx.camera:camera-core:1.1.0")
    implementation("androidx.camera:camera-camera2:1.1.0")
    implementation("androidx.camera:camera-lifecycle:1.1.0")
    implementation("androidx.camera:camera-view:1.1.0")

    // Firebase BoM 및 Analytics 제거
    // implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    // implementation("com.google.firebase:firebase-analytics")

    // JUnit 의존성 추가
    testImplementation("junit:junit:4.13.2")

    // Android 테스트 라이브러리 추가
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // OkHttp 의존성 추가
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Retrofit 의존성 추가
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Glide 의존성 추가
    implementation("com.github.bumptech.glide:glide:4.13.2")
    kapt("com.github.bumptech.glide:compiler:4.13.2")

    // Material CalendarView 추가
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")

    // ThreeTenABP 추가
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")

    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
}
