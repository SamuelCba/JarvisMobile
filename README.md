# JarvisMobile

Native Android/Kotlin MVP for a Jarvis-style mobile assistant.

The app starts with a practical command runner:

- typed or spoken commands
- app launching commands like "abre YouTube" or "abre Chrome"
- shortcut to Android Accessibility settings
- registered `AccessibilityService` stub for the next automation phase
- local memory of recent commands

## Continue work

Open Codex from:

```sh
cd /data/data/com.termux/files/home/JarvisMobile
```

Read `PROJECT_MEMORY.md` first. It stores the local workflow for repo setup, CI builds, APK delivery, and ADB install.

## Build

Local Android tooling is not required in Termux. Push to GitHub or run the workflow manually and let Actions build:

```sh
gh workflow run android-kotlin.yml
gh run watch --exit-status
```

The workflow uploads `jarvismobile-kotlin-debug-apk`.
