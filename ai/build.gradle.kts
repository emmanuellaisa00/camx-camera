plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.beadrop.ai"
    compileSdk = AppConfig.compileSdk
    defaultConfig { minSdk = AppConfig.minSdk }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = AppConfig.jvmTarget }
    androidResources {
        noCompress += "tflite"
    }
}

dependencies {
    implementation(project(":core"))
    
    implementation(Deps.hiltAndroid)
    ksp(Deps.hiltCompiler)
    
    implementation(Deps.tensorflowLite)
    implementation(Deps.tensorflowLiteGpu)
    implementation(Deps.tensorflowLiteSupport)
    
    implementation(Deps.coroutinesCore)
    implementation(Deps.coroutinesAndroid)
    
    testImplementation(Deps.junit)
    testImplementation(Deps.coroutinesTest)
}
