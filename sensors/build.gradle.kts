plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.beadrop.sensors"
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
    
    implementation(Deps.coroutinesCore)
    implementation(Deps.coroutinesAndroid)
    
    testImplementation(Deps.junit)
    testImplementation(Deps.coroutinesTest)
}
