# JarvisMobile Project Memory

JarvisMobile is a new Flutter app project created from Termux.

## Working rule

- Open Codex from this folder when continuing work:
  `/data/data/com.termux/files/home/JarvisMobile`
- Treat this as a Flutter mobile app project.
- Do not rely on local Flutter/Dart in Termux for builds. Prior projects in this device hit:
  `dart: cannot execute: required file not found`
- Use GitHub Actions as the real Android build environment.

## Build workflow

1. Keep source code in the repo:
   - `pubspec.yaml`
   - `lib/main.dart`
   - `.github/workflows/flutter-android.yml`
2. In GitHub Actions, generate the Android project files:
   - `flutter create --platforms=android .`
   - `rm -rf test`
   - `flutter pub get`
   - `flutter analyze`
   - `flutter build apk --release`
3. Upload this artifact:
   - `build/app/outputs/flutter-apk/app-release.apk`
4. If CI fails, inspect the failed logs first:
   - `gh run view <run_id> --log-failed`

## GitHub setup

- GitHub CLI is authenticated as `SamuelCba`.
- The token already has `workflow` scope.
- Expected repo name: `JarvisMobile`
- Expected remote creation command:
  `gh repo create JarvisMobile --public --source=. --remote=origin --push`

## APK delivery pattern

- Download Actions artifacts into:
  `/data/data/com.termux/files/usr/tmp/JarvisMobile/`
- Copy final APK into shared storage:
  `/storage/emulated/0/Documents/JarvisMobile/app-release.apk`
- If ADB install is requested:
  - run `adb devices`
  - run `adb install -r /storage/emulated/0/Documents/JarvisMobile/app-release.apk`
  - if `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, ask before uninstalling the previous package.

## Current intent

The user wants JarvisMobile to be a Flutter app and wants this folder to carry enough memory so a new Codex session opened here knows to create/push the repo and use GitHub Actions for builds.
