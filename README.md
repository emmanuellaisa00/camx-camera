# Beadrop Camera

> The most premium offline Android camera experience.

![Android](https://img.shields.io/badge/Android-10%2B-green)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-100%25-blue)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-orange)
![License](https://img.shields.io/badge/License-Proprietary-red)

---

## 🎯 Vision

Beadrop Camera combines **Samsung flagship power**, **Apple fluidity**, and **VisionOS glass depth** into a single premium Android camera application. Every interaction is deliberate and luxurious.

**Fully offline. No cloud. No backend. No account. No internet required.**

---

## 🏗️ Architecture

### Modular Architecture

```
BeadropCamera/
├── app/           → Application entry point, DI root, navigation host
├── core/          → Domain models, utilities, extensions
├── design/        → Custom design system, theme, glassmorphism components
├── camera/        → CameraX engine, capture, zoom, focus, exposure
├── sensors/       → Orientation, gyroscope, light sensors
├── gallery/       → Media browser, albums, batch operations
├── editor/        → Image editing engine, filters, tools
├── player/        → Hardware-accelerated video player
├── storage/       → Room database, MediaStore integration
├── ai/            → TensorFlow Lite inference, face detection, enhancement
├── navigation/    → Type-safe navigation destinations
├── settings/      → User preferences, DataStore
└── benchmark/     → Performance monitoring and metrics
```

### Technology Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin 2.0 |
| **UI** | 100% Jetpack Compose |
| **Design** | Material 3 + Custom Glassmorphism System |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt (Dagger) |
| **Camera** | CameraX + Camera2 Interop |
| **Database** | Room |
| **Media** | MediaStore API |
| **AI** | TensorFlow Lite |
| **Video** | Media3 ExoPlayer |
| **Async** | Coroutines + Flow |
| **Image Loading** | Coil |
| **Serialization** | Kotlinx Serialization |
| **Build** | Gradle KTS + Version Catalog |
| **CI/CD** | GitHub Actions |
| **Min SDK** | Android 10 (API 29) |
| **Target SDK** | Android 15 (API 35) |

---

## 📸 Camera Modes

| Mode | Description |
|------|-------------|
| **Photo** | Standard photo capture with auto optimization |
| **Video** | High quality video recording up to 4K/8K |
| **Portrait** | Depth-of-field portrait with AI segmentation |
| **Night** | Enhanced low-light photography |
| **Pro** | Full manual controls (ISO, shutter, focus, WB) |
| **Panorama** | Wide panoramic photography |
| **Slow Motion** | High frame rate slow-mo (120/240fps) |
| **Timelapse** | Time-lapse video recording |
| **Hyperlapse** | Stabilized moving time-lapse |
| **Macro** | Close-up macro photography |
| **Document** | Document scanning with perspective correction |

---

## 🔍 Zoom System

Flagship zoom with haptic detent stops:

| Zoom | Type | Features |
|------|------|----------|
| **0.5×** | Ultra Wide | Full scene capture |
| **1×** | Wide | Standard view |
| **2-3×** | Telephoto | Optical-quality zoom |
| **10×** | Super Zoom | + Floating navigator window |
| **30×** | Hyper Zoom | + Advanced targeting system |
| **100×** | Max Zoom | + Stabilization assistance overlay |

---

## 🎨 Design System

### Glassmorphism Foundation

Every major control uses frosted glass styling:
- Dynamic translucency with layer separation
- Light edge highlights and reflections
- Animated response to scroll, zoom, and state changes
- 5 intensity levels: Ultra Thin → Ultra Thick

### Animation System

All animations use **spring physics**:
- `Quick` — Button presses (high stiffness, medium bounce)
- `Standard` — UI transitions (medium stiffness)
- `Smooth` — Slow graceful transitions
- `Camera` — Viewfinder interactions
- `Focus` — Focus ring animations
- `Zoom` — Smooth zoom transitions
- `Sheet` — Bottom sheet expansion

### Haptic Feedback

Precision haptic patterns for every interaction:
- Light tap, medium impact, heavy impact
- Zoom stop detent haptics
- Focus lock confirmation
- Capture shutter feedback
- Error vibration patterns

---

## 🧠 AI Features (Offline)

All AI runs on-device using TensorFlow Lite:

- Face detection (BlazeFace)
- Subject tracking
- Portrait segmentation
- Scene recognition
- QR code scanning
- Document edge detection
- Auto image enhancement
- AI sharpening (Unsharp Mask)

---

## 🖼️ Gallery

- Grid view with 3-column layout
- Albums and folder organization
- Filter: All / Photos / Videos / Favorites / RAW
- Batch select, delete, export
- EXIF metadata viewer
- Search by filename

---

## ✂️ Editor

Non-destructive editing with full undo/redo:
- Crop & Rotate
- Exposure, Contrast, Saturation
- Brightness, Highlights, Shadows
- Sharpness & Blur
- Vignette & Grain
- Color filters (Noir, Vintage, Cool, Warm)
- Color curves per channel
- AI auto-enhance

---

## 🎬 Video Player

- Hardware-accelerated playback (ExoPlayer/Media3)
- Timeline scrubbing
- Playback speed (0.5x - 2x)
- Frame stepping (forward/back)
- Loop playback
- Rewind/Forward 10s

---

## 📊 Performance Targets

| Metric | Target | Status |
|--------|--------|--------|
| Cold start | < 300ms | ✅ Lazy Hilt init |
| Camera ready | < 500ms | ✅ CameraX optimized |
| UI frame rate | 60 FPS | ✅ Compose optimized |
| UI capable | 120 FPS | ✅ Spring animations |
| Memory | Low | ✅ Scoped lifecycle |
| Battery | Optimized | ✅ Sensor batching |

---

## ♿ Accessibility

- TalkBack / screen reader support
- Dynamic text scaling
- High contrast mode
- Comprehensive content descriptions
- Minimum 48dp touch targets
- Haptic feedback system

---

## 🔐 Security

- No network permissions required
- No data leaves the device
- No analytics or tracking
- No account system
- ProGuard/R8 minification in release
- Signed release builds

---

## 🚀 CI/CD Pipeline

```
Push → Lint → Unit Tests → UI Tests → Static Analysis → Build APK → Build AAB → Sign → Upload Artifacts
```

GitHub Actions workflow with:
- Debug and Release builds
- Artifact retention (14/30 days)
- Auto version generation from git history
- Changelog generation
- Keystore signing via secrets

---

## 📁 File Format Support

| Format | Type | Support |
|--------|------|---------|
| JPEG | Image | ✅ Capture + View |
| PNG | Image | ✅ Capture + View |
| WEBP | Image | ✅ Capture + View |
| HEIC | Image | ✅ Capture + View |
| RAW DNG | Image | ✅ View |
| MP4 | Video | ✅ Record + Play |

---

## 🛠️ Build

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires keystore)
./gradlew assembleRelease

# Run tests
./gradlew testDebugUnitTest

# Lint check
./gradlew lint
```

---

## 📄 License

Proprietary. All rights reserved.

---

*Built with precision. Engineered for premium.*
