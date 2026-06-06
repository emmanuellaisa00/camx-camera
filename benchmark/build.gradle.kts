plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.beadrop.benchmark"
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
    implementation(Deps.coroutinesCore)
}
