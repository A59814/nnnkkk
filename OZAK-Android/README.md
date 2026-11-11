# OZAK — Android project

OZAK is an Android WebView app with a native Android overlay bubble and native alarm scheduling.

## Build locally
1. Open this folder in Android Studio (recent version).
2. Let Gradle sync.
3. Build > Build Bundle(s) / APK(s) > Build APK(s).

Or with JDK 17 and Gradle 8.9:
```bash
gradle assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Build on GitHub
1. Create a new GitHub repository.
2. Upload the contents of this ZIP to the repository root (not the ZIP itself).
3. Push to `main`, or open Actions and run **Build OZAK APK**.
4. Open the successful workflow run and download the `OZAK-debug-apk` artifact.

## First launch on Android
- Grant notification permission when asked.
- Grant exact-alarm access if Android requests it.
- Tap **تفعيل الفقاعة** and allow **Display over other apps**.
- Some manufacturers (including Xiaomi/MIUI) may require enabling Autostart and allowing background activity/battery use for reliable delivery.

## Important limitations
- Android and device manufacturers can restrict full-screen alarm launches and background activity. The notification remains the fallback.
- Exact alarms require user-granted special access on Android 12+ on many devices.
- The current bubble is a native overlay bubble; tapping it opens OZAK. It does not yet provide a full voice panel over every app.
- This is an initial buildable starter project, not a Play Store production release. Test on your phone before relying on it for critical reminders.
- Debug APK is unsigned for Play Store distribution. A release build needs a signing key and secure GitHub secrets.
