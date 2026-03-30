# Copilot Instructions — BoingBallDemo

A Kotlin Multiplatform app targeting **Android and iOS** that recreates the classic Amiga "Boing Ball" demo with configurable Amiga OS styling.

---

## Build Commands

```bash
# Android debug APK
./gradlew :composeApp:assembleDebug

# All tests (common + platform)
./gradlew :composeApp:allTests

# Android unit tests only
./gradlew :composeApp:testDebugUnitTest

# Single test
./gradlew :composeApp:testDebugUnitTest --tests "com.rff.boingballdemo.ComposeAppCommonTest.example"
```

iOS builds are done via Xcode (open `/iosApp`) or the IDE run configuration.

---

## Architecture

### Source Sets

| Source set | Purpose |
|---|---|
| `commonMain` | All app logic, UI, and shared interfaces |
| `androidMain` | Android `actual` implementations + `MainActivity` |
| `nativeMain` | Shared iOS/Apple `actual` implementations (used by both `iosArm64` and `iosSimulatorArm64`) |
| `iosMain` | iOS entry point only (`MainViewController.kt`) |

`nativeMain` is the correct place for Apple platform implementations — **not** `iosMain`.

### MVI-style Screen Pattern

Every screen follows this structure:

```
XxxState.kt        — immutable data class, all UI state
XxxAction.kt       — sealed interface of user intents
XxxViewModel.kt    — ViewModel, holds StateFlow<XxxState>, handles actions
XxxScreen.kt       — two composables:
                     XxxScreenRoot  — wires ViewModel via koinViewModel()
                     XxxScreen      — pure composable, takes state + onAction lambda
```

`XxxScreenRoot` is the navigation entry point. `XxxScreen` is the testable, preview-friendly composable.

### Navigation

Uses **Navigation 3** (`org.jetbrains.androidx.navigation3`). Routes are a `@Serializable sealed interface`:

```kotlin
@Serializable sealed interface AppRoute : NavKey
@Serializable data object Home : AppRoute
@Serializable data object Prefs : AppRoute
```

`NavigationRoot()` in `Navigation.kt` owns the `NavBackStack` and `NavDisplay`. Add new routes to the sealed interface and handle them in the `entryProvider` lambda.

### Dependency Injection (Koin 4.x)

- `sharedModule` in `di/AppModule.kt` — common singletons and ViewModels
- `expect val platformModule: Module` — platform-specific bindings (`actual` in `androidMain` and `nativeMain`)
- `initKoin()` in `di/DiUtils.kt` — call from each platform entry point
- ViewModels registered with `viewModelOf(::XxxViewModel)`, retrieved in composables with `koinViewModel()`

### Persistence

`AppSettings` wraps `DataStore<Preferences>`. `createPreferencesDataStore()` is an `expect fun` with platform `actual` implementations:
- Android: uses `Context.filesDir`
- Native: uses `NSDocumentDirectory`

---

## Key Conventions

### Compose Multiplatform: Fonts

`org.jetbrains.compose.resources.Font` is `@Composable` and **cannot be called at the top level**. Font families and Typography must be created inside composable functions:

```kotlin
// ✅ Correct
@Composable fun topazFont() = FontFamily(Font(Res.font.topaz_a500, FontWeight.Normal))

@Composable fun appTypography(): Typography { val f = topazFont(); return Typography(...) }

// ❌ Wrong — crashes at runtime
val TopazFont = FontFamily(Font(Res.font.topaz_a500))
```

Font files live in `composeApp/src/commonMain/composeResources/font/`.

### Compose Multiplatform: Theme

`BoingBallDemoTheme` is in `commonMain`. Platform-specific color scheme logic uses `expect`/`actual`:

```kotlin
// commonMain: expect fun getColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme
// androidMain: supports dynamic color (Android 12+)
// nativeMain: static dark/light only
```

### `expect`/`actual` Targets

Three things currently have platform splits:

| Interface | commonMain | androidMain | nativeMain |
|---|---|---|---|
| `createPreferencesDataStore()` | `expect fun` | Context-based path | NSDocumentDirectory path |
| `BoingBallAudioPlayer` | `expect class` | Android AudioPlayer | Native AudioPlayer |
| `getColorScheme()` | `expect fun` | Dynamic color | Static dark/light |

### Previews

All previews use `BoingBallDemoTheme` as the wrapper and are `private`:

```kotlin
@Preview
@Composable
private fun MyScreenPreview() {
    BoingBallDemoTheme { MyScreen(previewState) }
}
```

### UI Components

Reusable Amiga-styled widgets are in `component/`. The app supports two visual modes via `OSStyle`:

- `OSStyle.AmigaOS13` — Amiga OS 1.3 look (blue/orange palette)
- `OSStyle.AmigaOS20` — Amiga OS 2.0 look (grey palette)

Components accept `OSStyle` to render the correct style. Colors for each style are defined in `ui/theme/Color.kt`.

---

## Key Dependencies (versions in `gradle/libs.versions.toml`)

- Compose Multiplatform `1.10.0`
- Kotlin `2.3.0`
- Koin `4.2.0` / `koin-compose-multiplatform 4.2.0`
- Navigation 3 (`org.jetbrains.androidx.navigation3`) `1.0.0-alpha06`
- DataStore `1.2.1`
- `androidx.lifecycle` ViewModel `2.10.0`
