plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.beadrop.navigation"
    compileSdk = AppConfig.compileSdk
    defaultConfig { minSdk = AppConfig.minSdk }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = AppConfig.jvmTarget }
    buildFeatures { compose = true }
}

dependencies {
    implementation(project(":core"))
    
    implementation(platform(Deps.composeBom))
    implementation(Deps.composeUi)
    implementation(Deps.composeAnimation)
    implementation(Deps.navigationCompose)
    implementation(Deps.serialization)
}
