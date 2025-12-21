# Deployment Guide

## Pre-Deployment Checklist

- [ ] Update version code and version name in `build.gradle.kts`
- [ ] Update changelog
- [ ] Verify all tests pass: `./gradlew test`
- [ ] Run lint checks: `./gradlew lint`
- [ ] Test on multiple device sizes
- [ ] Verify API endpoints are correct

## Version Numbering

Following semantic versioning: MAJOR.MINOR.PATCH

## Building Release APK

### 1. Update Version

Edit `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 2
    versionName = "1.1.0"
}
```

### 2. Create Signing Key

```bash
keytool -genkey -v -keystore release.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias release
```

### 3. Build Release APK

```bash
./gradlew clean assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### 4. Build Bundle for Play Store

```bash
./gradlew clean bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

## Google Play Store Deployment

1. Go to [Google Play Console](https://play.google.com/console)
2. Create new app or select existing
3. Upload AAB file
4. Fill in app details and screenshots
5. Submit for review

## Post-Deployment Monitoring

- Monitor crash reports in Play Console
- Check user reviews
- Track installation metrics
