plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.beadrop.core"
    compileSdk = AppConfig.compileSdk
    defaultConfig {
        minSdk = AppConfig.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = AppConfig.jvmTarget }
    buildFeatures { compose = true }
}

dependencies {
    api(Deps.coreKtx)
    api(Deps.coroutinesCore)
    api(Deps.coroutinesAndroid)
    api(Deps.serialization)
    
    implementation(platform(Deps.composeBom))
    implementation(Deps.composeRuntime)
    
    implementation(Deps.hiltAndroid)
    ksp(Deps.hiltCompiler)
    
    implementation(Deps.roomRuntime)
    implementation(Deps.roomKtx)
    ksp(Deps.roomCompiler)
    
    implementation(Deps.dataStorePrefs)
    
    testImplementation(Deps.junit)
    testImplementation(Deps.coroutinesTest)
    testImplementation(Deps.mockk)
    testImplementation(Deps.turbine)
}
