# JarvisMobile

Flutter mobile app built from Termux with GitHub Actions as the Android build environment.

## Continue work

Open Codex from:

```sh
cd /data/data/com.termux/files/home/JarvisMobile
```

Read `PROJECT_MEMORY.md` first. It stores the local workflow for repo setup, CI builds, APK delivery, and ADB install.

## Build

Local Flutter is not required in Termux. Push to GitHub and let Actions run:

```sh
flutter create --platforms=android .
flutter pub get
flutter analyze
flutter build apk --release
```

The workflow uploads `jarvismobile-release-apk`.
