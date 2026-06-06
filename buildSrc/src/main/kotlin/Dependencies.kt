object Versions {
    const val compose = "1.7.0"
    const val composeMaterial3 = "1.3.0"
    const val composeNavigation = "2.8.0"
    const val lifecycle = "2.8.4"
    const val cameraX = "1.4.0"
    const val hilt = "2.51.1"
    const val hiltNavCompose = "1.2.0"
    const val room = "2.6.1"
    const val coroutines = "1.8.1"
    const val coil = "2.7.0"
    const val tensorflow = "2.16.1"
    const val mediaPipe = "0.10.14"
    const val exifInterface = "1.3.7"
    const val dataStore = "1.1.1"
    const val media3 = "1.4.0"
    const val accompanist = "0.34.0"
    const val serialization = "1.7.1"
    const val renderscript = "1.0.0-beta01"
}

object Deps {
    // Compose BOM
    const val composeBom = "androidx.compose:compose-bom:2024.08.00"
    
    // Compose
    const val composeUi = "androidx.compose.ui:ui"
    const val composeUiGraphics = "androidx.compose.ui:ui-graphics"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeUiUtil = "androidx.compose.ui:ui-util"
    const val composeMaterial3 = "androidx.compose.material3:material3"
    const val composeMaterialIcons = "androidx.compose.material:material-icons-extended"
    const val composeFoundation = "androidx.compose.foundation:foundation"
    const val composeAnimation = "androidx.compose.animation:animation"
    const val composeRuntime = "androidx.compose.runtime:runtime"
    
    // Activity & Navigation
    const val activityCompose = "androidx.activity:activity-compose:1.9.1"
    const val navigationCompose = "androidx.navigation:navigation-compose:${Versions.composeNavigation}"
    
    // Lifecycle
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-compose:${Versions.lifecycle}"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}"
    
    // CameraX
    const val cameraXCore = "androidx.camera:camera-core:${Versions.cameraX}"
    const val cameraXCamera2 = "androidx.camera:camera-camera2:${Versions.cameraX}"
    const val cameraXLifecycle = "androidx.camera:camera-lifecycle:${Versions.cameraX}"
    const val cameraXVideo = "androidx.camera:camera-video:${Versions.cameraX}"
    const val cameraXView = "androidx.camera:camera-view:${Versions.cameraX}"
    const val cameraXExtensions = "androidx.camera:camera-extensions:${Versions.cameraX}"
    
    // Hilt
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
    const val hiltNavCompose = "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavCompose}"
    
    // Room
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    
    // Coroutines
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    
    // Coil
    const val coilCompose = "io.coil-kt:coil-compose:${Versions.coil}"
    const val coilVideo = "io.coil-kt:coil-video:${Versions.coil}"
    const val coilGif = "io.coil-kt:coil-gif:${Versions.coil}"
    
    // TensorFlow Lite
    const val tensorflowLite = "org.tensorflow:tensorflow-lite:${Versions.tensorflow}"
    const val tensorflowLiteGpu = "org.tensorflow:tensorflow-lite-gpu:${Versions.tensorflow}"
    const val tensorflowLiteSupport = "org.tensorflow:tensorflow-lite-support:0.4.4"
    
    // Media3 / ExoPlayer
    const val media3ExoPlayer = "androidx.media3:media3-exoplayer:${Versions.media3}"
    const val media3Ui = "androidx.media3:media3-ui:${Versions.media3}"
    const val media3Session = "androidx.media3:media3-session:${Versions.media3}"
    
    // DataStore
    const val dataStorePrefs = "androidx.datastore:datastore-preferences:${Versions.dataStore}"
    
    // ExifInterface
    const val exifInterface = "androidx.exifinterface:exifinterface:${Versions.exifInterface}"
    
    // Serialization
    const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}"
    
    // Core
    const val coreKtx = "androidx.core:core-ktx:1.13.1"
    const val coreSplashscreen = "androidx.core:core-splashscreen:1.0.1"
    const val appcompat = "androidx.appcompat:appcompat:1.7.0"
    const val windowManager = "androidx.window:window:1.3.0"
    
    // Testing
    const val junit = "junit:junit:4.13.2"
    const val junitExt = "androidx.test.ext:junit:1.2.1"
    const val espressoCore = "androidx.test.espresso:espresso-core:3.6.1"
    const val composeUiTest = "androidx.compose.ui:ui-test-junit4"
    const val composeUiTestManifest = "androidx.compose.ui:ui-test-manifest"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    const val mockk = "io.mockk:mockk:1.13.12"
    const val turbine = "app.cash.turbine:turbine:1.1.0"
}
