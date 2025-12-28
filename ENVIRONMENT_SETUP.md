# 👨💻 Environment Setup Guide - NoghreSod Android

## Table of Contents
1. [System Requirements](#system-requirements)
2. [Java/JDK Setup](#javajdk-setup)
3. [Android SDK Setup](#android-sdk-setup)
4. [IDE Configuration](#ide-configuration)
5. [Project Configuration](#project-configuration)
6. [Verification Checklist](#verification-checklist)
7. [Troubleshooting](#troubleshooting)

---

## System Requirements

### Minimum Specifications

| Component | Minimum | Recommended | Notes |
|-----------|---------|-------------|-------|
| **OS** | macOS 10.14+, Windows 10, Ubuntu 18.04+ | macOS 12+, Windows 11, Ubuntu 22.04+ | 64-bit required |
| **RAM** | 8 GB | 16 GB | 4 GB allocated to Gradle |
| **Disk** | 15 GB | 50+ GB | For SDK, emulator, builds |
| **CPU** | Dual-core | Quad-core+ | Intel/ARM, performance varies |
| **JDK** | 17+ | 17 LTS | OpenJDK recommended |

### Internet Connection
- **First Setup:** 2-3 GB download (SDK, dependencies)
- **Subsequent:** ~100 MB per project update
- **Recommended:** Wired connection for stability

---

## Java/JDK Setup

### macOS (Homebrew)

```bash
# Install JDK 17
brew install openjdk@17

# Link to system
sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

# Verify
java -version
# Output: openjdk version "17.0.x" 2024-xx-xx

# Set JAVA_HOME
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc
```

### Windows (Chocolatey)

```powershell
# Install JDK 17
choco install openjdk17

# Verify
java -version

# Set JAVA_HOME (if not auto-set)
# System Properties → Environment Variables
# Add: JAVA_HOME = C:\Program Files\OpenJDK\jdk-17.x.x
```

### Linux (Ubuntu/Debian)

```bash
# Install JDK 17
sudo apt-get update
sudo apt-get install openjdk-17-jdk openjdk-17-jdk-headless

# Verify
java -version

# Set JAVA_HOME (optional, usually auto-set)
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

### Verify Installation

```bash
# Check version (should be 17+)
java -version
javac -version

# Check JAVA_HOME
echo $JAVA_HOME
# Should output: /path/to/java/17

# Verify in Gradle
./gradlew javaToolchains
```

---

## Android SDK Setup

### Option 1: Android Studio (Recommended)

1. **Download** Android Studio from [developer.android.com](https://developer.android.com/studio)
2. **Install** following official setup wizard
3. **Launch** Android Studio
4. **SDK Manager** opens automatically:
   - Android SDK Platform 34 (latest)
   - Android SDK Build-Tools 34.x.x
   - Android Emulator
   - Android SDK Platform-Tools

### Option 2: Command Line Setup

```bash
# Install Android SDK Command Line Tools
wget https://dl.google.com/android/repository/commandlinetools-linux-10135233_latest.zip
unzip commandlinetools-*.zip
mv cmdline-tools ~/android-sdk/

# Set ANDROID_HOME
echo 'export ANDROID_HOME=$HOME/android-sdk' >> ~/.bashrc
echo 'export PATH=$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools:$PATH' >> ~/.bashrc
source ~/.bashrc

# Install SDK packages
sdkmanager --install "platforms;android-34"
sdkmanager --install "build-tools;34.0.0"
sdkmanager --install "emulator"
sdkmanager --install "platform-tools"
```

### Verify Android SDK

```bash
# Check ANDROID_HOME
echo $ANDROID_HOME

# List installed packages
sdkmanager --list_installed

# Should include:
# - Android SDK Platform 34
# - Android SDK Build-Tools 34.x
# - Android Emulator
# - Platform Tools
```

---

## IDE Configuration

### Android Studio Setup

#### 1. JDK Configuration

```
File → Project Structure → SDK Location

Set:
- JDK location: /path/to/java/17 (or auto-detect)
- Android SDK location: $ANDROID_HOME
- NDK location: (optional, not needed for this project)
```

#### 2. Gradle Configuration

```
File → Settings → Build, Execution, Deployment → Gradle

Set:
- Gradle JVM: Project JDK (or JDK 17)
- Use offline mode: unchecked (unless intentional)
- Gradle VM options: -Xmx6g (or adjust for your machine)
```

#### 3. Memory Settings

Edit `$ANDROID_STUDIO_HOME/bin/studio.vmoptions` (or `studio64.exe.vmoptions` on Windows):

```
-Xms2g
-Xmx8g
-XX:MaxMetaspaceSize=2g
-XX:ReservedCodeCacheSize=512m
```

Then restart Android Studio.

#### 4. Plugin Configuration

Optional plugins for better development:

```
Settings → Plugins → Browse Repositories

Install:
- Kotlin
- Gradle
- Hilt
- Protocol Buffers
- Save Actions (optional)
```

### VS Code Setup (Alternative)

If using VS Code for Kotlin editing:

1. Install extensions:
   - Kotlin Language (fwcd)
   - Android Tools (adelphimediatech)
   - Gradle for Java (vscjava.gradle-for-java)

2. Configure `.vscode/settings.json`:

```json
{
  "java.home": "/path/to/java/17",
  "java.jdt.ls.vmargs": "-Xmx2g",
  "gradle.javaPath": "/path/to/java/17",
  "android.sdkPath": "/path/to/android/sdk"
}
```

---

## Project Configuration

### 1. Clone Repository

```bash
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android
```

### 2. Setup Secrets

```bash
# Copy example
cp local.properties.example local.properties

# Edit with your credentials
vim local.properties
```

Required properties:
```properties
zarinpal.merchant.id=YOUR_PRODUCTION_ID
zarinpal.sandbox.merchant.id=YOUR_SANDBOX_ID
```

See [SETUP_SECRETS.md](SETUP_SECRETS.md) for complete setup.

### 3. Gradle Wrapper

```bash
# Already in repository, no setup needed
# Just verify
./gradlew --version
# Should output: Gradle 8.0+
```

### 4. Initial Gradle Sync

```bash
# Download dependencies
./gradlew clean build

# In Android Studio:
# File → Sync Now
# Wait for indexing to complete
```

---

## Verification Checklist

### Pre-Development Checklist

```bash
# 1. Java/JDK
java -version          # Should be 17+
echo $JAVA_HOME        # Should be set

# 2. Android SDK
echo $ANDROID_HOME     # Should be set
ls $ANDROID_HOME/platforms/android-34  # Should exist
ls $ANDROID_HOME/build-tools           # Should exist

# 3. Gradle
./gradlew --version    # Should be 8.0+

# 4. Project Configuration
ls local.properties    # Should exist
grep zarinpal local.properties  # Should show IDs

# 5. IDE
# Android Studio: Help → About → Should open without errors

# 6. Build
./gradlew assembleDebug  # Should complete successfully
```

### Run Tests

```bash
# Unit tests
./gradlew test

# Should see:
# "BUILD SUCCESSFUL"
# "97 tests passed"
```

### Run App

```bash
# On emulator or device
./gradlew installDebug
adb shell am start -n com.noghre.sod.debug/.MainActivity

# Should launch app without crashes
```

---

## Environment Variables Summary

### Linux/macOS

Add to `~/.bashrc` or `~/.zshrc`:

```bash
# Java
export JAVA_HOME=/path/to/java/17
export PATH=$JAVA_HOME/bin:$PATH

# Android
export ANDROID_HOME=$HOME/android-sdk
export PATH=$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH

# Gradle (optional)
export GRADLE_USER_HOME=$HOME/.gradle
export GRADLE_OPTS="-Xmx6g"
```

Then reload:
```bash
source ~/.bashrc  # or ~/.zshrc for zsh
```

### Windows

Set via System Properties:

```
System Properties → Environment Variables

User variables:
- JAVA_HOME = C:\Program Files\Java\openjdk-17
- ANDROID_HOME = C:\Users\YourName\AppData\Local\Android\Sdk
- GRADLE_USER_HOME = C:\Users\YourName\.gradle

System variables (add to PATH):
- C:\Program Files\Java\openjdk-17\bin
- C:\Users\YourName\AppData\Local\Android\Sdk\platform-tools
- C:\Users\YourName\AppData\Local\Android\Sdk\tools
```

---

## Performance Tuning

### For 8GB RAM Machine

```bash
# gradle.properties
org.gradle.jvmargs=-Xmx3072m

# Android Studio (studio.vmoptions)
-Xms1024m
-Xmx4g
```

### For 16GB+ RAM Machine

```bash
# gradle.properties
org.gradle.jvmargs=-Xmx8192m

# Android Studio (studio.vmoptions)
-Xms2g
-Xmx8g
```

---

## Troubleshooting

### JDK Not Found

```bash
# Error: Unable to locate a JavaSDK

# Solution:
# 1. Install JDK 17
brew install openjdk@17

# 2. Set JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# 3. Verify
java -version
```

### Android SDK Not Found

```bash
# Error: SDK location not found

# Solution:
# In Android Studio:
# File → Project Structure → SDK Location
# Point to correct Android SDK path
```

### Gradle Build Fails

```bash
# Error: Gradle sync failed

# Solution:
./gradlew clean
rm -rf ~/.gradle/
./gradlew build
```

### Out of Memory During Build

```bash
# Error: Java heap space

# Solution:
# Increase JVM memory in gradle.properties
org.gradle.jvmargs=-Xmx8192m
```

---

## Next Steps

After completing this setup:

1. 🔐 [Configure Payment Secrets](SETUP_SECRETS.md)
2. 🏗️ [Build the Project](BUILD_AND_REBUILD_GUIDE.md)
3. 🚀 [Run the App](QUICK_START.md)
4. 🤫 [Run Tests](BUILD_AND_REBUILD_GUIDE.md#tests)

---

**Last Updated:** 2025-12-28
**Status:** ✅ Production-Ready
**Support:** See GitHub Issues for help
