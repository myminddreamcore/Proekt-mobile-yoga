plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    //ViewBinding - привязка элементов XML к коду
    // Безопаснее и быстрее чем findViewById()
    buildFeatures {
        viewBinding = true
    }
    namespace = "com.example.proekt"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.proekt"
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.squareup.picasso:picasso:2.8")
    //OkHttp - библиотека для HTTP запросов
// Позволяет отправлять GET, POST, PUT, DELETE запросы
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
//Logging Interceptor - логирование HTTP запросов/ответов
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
//Gson - преобразование JSON в объекты Kotlin и наоборот
// Нужен для парсинга JSON ответов от сервера
    implementation("com.google.code.gson:gson:2.10.1")
//Coroutines - асинхронное программирование
// Позволяет не блокировать UI при работе с сетью
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
//Material Design - красивые UI компоненты
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}