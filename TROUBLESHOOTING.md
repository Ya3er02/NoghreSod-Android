# Troubleshooting Guide

## Build Issues

### Gradle Sync Fails

**Solution**:
1. **File** → **Invalidate Caches** → **Invalidate and Restart**
2. Delete `.gradle` folder
3. Run: `./gradlew clean build`

### Out of Memory

**Solution**: Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m
```

## Runtime Issues

### App Crashes on Launch

**Check Logcat**:
```bash
adb logcat | grep FATAL
```

### API Connection Fails

**Check**:
1. API URL in `di/NetworkModule.kt`
2. Internet permission in AndroidManifest.xml
3. Network connectivity

### UI Not Updating

**Checklist**:
- [ ] StateFlow properly initialized
- [ ] collectAsState() used in Composable
- [ ] State updates in ViewModel

## Testing Issues

### Tests Won't Run

**Solution**:
1. Check test file location: `src/test/kotlin/...`
2. Verify `@Test` annotation present
3. Run: `./gradlew test`

## Device Issues

### Device Not Detected

**Solution**:
1. Enable USB debugging on device
2. Accept USB authorization
3. Try: `adb kill-server && adb start-server`

## Performance Issues

### Slow Scrolling

**Check**:
1. Use LazyColumn instead of Column
2. Minimize recompositions
3. Profile with Android Profiler

### Memory Leaks

**Install LeakCanary**:
```gradle
debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.12'
```
