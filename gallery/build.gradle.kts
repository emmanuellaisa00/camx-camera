plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    namespace = "com.beadrop.gallery"
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
    implementation(project(":design"))
    implementation(project(":storage"))
    
    implementation(platform(Deps.composeBom))
    implementation(Deps.composeUi)
    implementation(Deps.composeMaterial3)
    implementation(Deps.composeAnimation)
    implementation(Deps.composeFoundation)
    
    implementation(Deps.lifecycleRuntime)
    implementation(Deps.lifecycleViewModel)
    
    implementation(Deps.hiltAndroid)
    ksp(Deps.hiltCompiler)
    implementation(Deps.hiltNavCompose)
    
    implementation(Deps.coilCompose)
    implementation(Deps.coilVideo)
    implementation(Deps.coilGif)
    
    implementation(Deps.exifInterface)
    
    testImplementation(Deps.junit)
    testImplementation(Deps.coroutinesTest)
    testImplementation(Deps.mockk)
}
