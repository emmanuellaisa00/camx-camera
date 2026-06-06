plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.beadrop.design"
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
    
    api(platform(Deps.composeBom))
    api(Deps.composeUi)
    api(Deps.composeUiGraphics)
    api(Deps.composeUiUtil)
    api(Deps.composeMaterial3)
    api(Deps.composeMaterialIcons)
    api(Deps.composeFoundation)
    api(Deps.composeAnimation)
    api(Deps.composeRuntime)
    
    debugImplementation(Deps.composeUiTooling)
    implementation(Deps.composeUiToolingPreview)
}
