package com.beadrop.camera

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.beadrop.camera.ui.CameraScreen
import com.beadrop.design.theme.BeadropTheme
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.gallery.ui.GalleryScreen
import com.beadrop.navigation.BeadropDestination
import com.beadrop.settings.ui.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission results
        allPermissionsGranted = permissions.values.all { it }
    }

    private var allPermissionsGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before calling super
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)

        // Edge-to-edge immersive experience
        enableEdgeToEdge()
        setupImmersiveMode()

        // Request permissions
        requestRequiredPermissions()

        setContent {
            BeadropTheme {
                BeadropApp(
                    allPermissionsGranted = allPermissionsGranted,
                    onRequestPermissions = { requestRequiredPermissions() },
                )
            }
        }
    }

    private fun setupImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun requestRequiredPermissions() {
        val permissions = buildList {
            add(Manifest.permission.CAMERA)
            add(Manifest.permission.RECORD_AUDIO)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
                add(Manifest.permission.READ_MEDIA_VIDEO)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    override fun onResume() {
        super.onResume()
        setupImmersiveMode()
    }
}

@Composable
fun BeadropApp(
    allPermissionsGranted: Boolean,
    onRequestPermissions: () -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "camera",
        modifier = Modifier
            .fillMaxSize()
            .background(ColorTokens.SurfacePure),
        enterTransition = { fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) },
        exitTransition = { fadeOut(animationSpec = androidx.compose.animation.core.tween(200)) },
    ) {
        composable("camera") {
            CameraScreen(
                onNavigateToGallery = { navController.navigate("gallery") },
                onNavigateToSettings = { navController.navigate("settings") },
            )
        }

        composable("gallery") {
            GalleryScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenMedia = { mediaItem ->
                    if (mediaItem.isVideo) {
                        navController.navigate("player/${mediaItem.id}/${java.net.URLEncoder.encode(mediaItem.uri, "UTF-8")}")
                    } else {
                        navController.navigate("viewer/${mediaItem.id}/${java.net.URLEncoder.encode(mediaItem.uri, "UTF-8")}")
                    }
                },
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable("viewer/{mediaId}/{uri}") { backStackEntry ->
            // Image viewer — will expand in production
            val mediaId = backStackEntry.arguments?.getString("mediaId")?.toLongOrNull() ?: 0L
            val uri = backStackEntry.arguments?.getString("uri") ?: ""
            // ImageViewerScreen(mediaId = mediaId, uri = uri)
        }

        composable("player/{mediaId}/{uri}") { backStackEntry ->
            // Video player — will expand in production
            val mediaId = backStackEntry.arguments?.getString("mediaId")?.toLongOrNull() ?: 0L
            val uri = backStackEntry.arguments?.getString("uri") ?: ""
            // VideoPlayerScreen(mediaId = mediaId, uri = uri)
        }

        composable("editor/{mediaId}/{uri}") { backStackEntry ->
            // Editor — will expand in production
            val mediaId = backStackEntry.arguments?.getString("mediaId")?.toLongOrNull() ?: 0L
            val uri = backStackEntry.arguments?.getString("uri") ?: ""
            // EditorScreen(mediaId = mediaId, uri = uri)
        }
    }
}
