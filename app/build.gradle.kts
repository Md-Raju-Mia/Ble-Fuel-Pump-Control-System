plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.test.bleproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.test.bleproject"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.filament.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


  //  implementation(platform(libs.firebase.bom))

   // implementation(libs.firebase.analytics)
   // implementation(libs.firebase.messaging)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //dependency for generating QR & Scanning QR
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.2.0")



    implementation ("com.google.mlkit:barcode-scanning:17.0.3")

    implementation ("androidx.camera:camera-core:1.1.0")
    implementation ("androidx.camera:camera-camera2:1.1.0")
    implementation ("androidx.camera:camera-lifecycle:1.1.0")
    implementation ("androidx.camera:camera-view:1.0.0-alpha31")
    implementation ("com.google.mlkit:barcode-scanning:17.0.3")




    implementation (libs.escpos.thermalprinter.android)








}

//apply(plugin = "com.google.gms.google-services")