plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.rabotamb"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.rabotamb"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // AndroidX và Material Design
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)

    // Lifecycle Components
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // Gson
    implementation("com.google.code.gson:gson:2.8.9")
    implementation ("com.squareup.picasso:picasso:2.8")


    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation ("com.google.android.material:material:1.5.0")

    implementation ("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}