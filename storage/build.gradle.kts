plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.beadrop.storage"
    compileSdk = AppConfig.compileSdk
    defaultConfig { minSdk = AppConfig.minSdk }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = AppConfig.jvmTarget }
}

dependencies {
    implementation(project(":core"))
    
    implementation(Deps.hiltAndroid)
    ksp(Deps.hiltCompiler)
    
    implementation(Deps.roomRuntime)
    implementation(Deps.roomKtx)
    ksp(Deps.roomCompiler)
    
    implementation(Deps.exifInterface)
    implementation(Deps.serialization)
    
    testImplementation(Deps.junit)
    testImplementation(Deps.coroutinesTest)
    testImplementation(Deps.mockk)
}
