plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    namespace = AppConfig.applicationId
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = AppConfig.applicationId
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH") ?: "keystore/release.jks"
            storeFile = file(keystorePath)
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = System.getenv("KEY_ALIAS") ?: ""
            keyPassword = System.getenv("KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = AppConfig.jvmTarget
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":design"))
    implementation(project(":camera"))
    implementation(project(":sensors"))
    implementation(project(":gallery"))
    implementation(project(":editor"))
    implementation(project(":player"))
    implementation(project(":storage"))
    implementation(project(":ai"))
    implementation(project(":navigation"))
    implementation(project(":settings"))

    // Compose BOM
    implementation(platform(Deps.composeBom))
    implementation(Deps.composeUi)
    implementation(Deps.composeUiGraphics)
    implementation(Deps.composeMaterial3)
    implementation(Deps.composeMaterialIcons)
    implementation(Deps.composeFoundation)
    implementation(Deps.composeAnimation)
    implementation(Deps.composeRuntime)
    implementation(Deps.activityCompose)
    implementation(Deps.navigationCompose)
    debugImplementation(Deps.composeUiTooling)
    implementation(Deps.composeUiToolingPreview)

    // Core
    implementation(Deps.coreKtx)
    implementation(Deps.coreSplashscreen)
    implementation(Deps.appcompat)

    // Lifecycle
    implementation(Deps.lifecycleRuntime)
    implementation(Deps.lifecycleViewModel)

    // Hilt
    implementation(Deps.hiltAndroid)
    ksp(Deps.hiltCompiler)
    implementation(Deps.hiltNavCompose)

    // Coroutines
    implementation(Deps.coroutinesCore)
    implementation(Deps.coroutinesAndroid)

    // DataStore
    implementation(Deps.dataStorePrefs)

    // Serialization
    implementation(Deps.serialization)

    // Window
    implementation(Deps.windowManager)

    // Testing
    testImplementation(Deps.junit)
    testImplementation(Deps.coroutinesTest)
    testImplementation(Deps.mockk)
    testImplementation(Deps.turbine)
    androidTestImplementation(Deps.junitExt)
    androidTestImplementation(Deps.espressoCore)
    androidTestImplementation(platform(Deps.composeBom))
    androidTestImplementation(Deps.composeUiTest)
    debugImplementation(Deps.composeUiTestManifest)
}
