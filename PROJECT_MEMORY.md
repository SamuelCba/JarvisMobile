# JarvisMobile Project Memory

JarvisMobile is a native Android/Kotlin app project created from Termux.

## Working rule

- Open Codex from this folder when continuing work:
  `/data/data/com.termux/files/home/JarvisMobile`
- Treat this as a native Android/Kotlin mobile app project.
- Do not rely on local Android/Flutter tooling in Termux for builds.
- Use GitHub Actions as the real Android build environment.
- The product direction is a Jarvis/Voice Access style assistant:
  voice input, command parsing, app launching, AccessibilityService automation,
  and later learned routines.

## Build workflow

1. Keep source code in the repo:
   - `settings.gradle.kts`
   - `build.gradle.kts`
   - `app/build.gradle.kts`
   - `app/src/main/...`
   - `.github/workflows/android-kotlin.yml`
2. In GitHub Actions:
   - set up Java 17
   - set up Android SDK
   - set up Gradle
   - run `gradle assembleDebug`
3. Upload this artifact:
   - `app/build/outputs/apk/debug/app-debug.apk`
4. If CI fails, inspect the failed logs first:
   - `gh run view <run_id> --log-failed`

## GitHub setup

- GitHub CLI is authenticated as `SamuelCba`.
- The token already has `workflow` scope.
- Expected repo name: `JarvisMobile`
- Remote: `https://github.com/SamuelCba/JarvisMobile.git`

## APK delivery pattern

- Download Actions artifacts into:
  `/data/data/com.termux/files/usr/tmp/JarvisMobile/`
- Copy final APK into shared storage:
  `/storage/emulated/0/Documents/JarvisMobile/app-debug.apk`
- If ADB install is requested:
  - run `adb devices`
  - run `adb install -r /storage/emulated/0/Documents/JarvisMobile/app-debug.apk`
  - if `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, ask before uninstalling the previous package.

## Current intent

The user decided the real MVP should be Kotlin native, because AccessibilityService and voice/system automation matter more than Flutter UI for this project.
