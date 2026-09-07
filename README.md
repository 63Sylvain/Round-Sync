# Round Sync - Rclone for Android

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](./LICENSE)
[![GitHub release](https://img.shields.io/github/v/release/63Sylvain/Round-Sync?include_prereleases)](https://github.com/63Sylvain/Round-Sync/releases/latest)
[![Android CI Build](https://github.com/63Sylvain/Round-Sync/actions/workflows/build-release.yml/badge.svg)](https://github.com/63Sylvain/Round-Sync/actions/workflows/build-release.yml)
[![Target SDK: Android 15 (API 35)](https://img.shields.io/badge/Target%20SDK-35%20(Android%2015)-brightgreen.svg)](https://developer.android.com/about/versions/15)
[![Rclone Version](https://img.shields.io/badge/Rclone-v1.75.1--extra-orange.svg)](https://github.com/gulp79/rclone-extra)

> ⚠️ **Fork Disclaimer**  
> This is an enhanced fork of [Round Sync](https://github.com/gulp79/Round-Sync) (originally by [newhinton](https://github.com/newhinton/Round-Sync) & [x0b](https://github.com/x0b)).
> All backends and native binaries are based on [rclone-extra](https://github.com/gulp79/rclone-extra) (Rclone v1.75.1).
> 
> *This repository is a modernized and high-performance edition of Round-Sync, delivering full Android 14/15 compatibility, massive Google Drive transfer speed optimizations (v2.5.9), seamless 1-click Google Drive sign-in, and critical bug fixes.*

---

## 🌟 Key Features & Modern Enhancements

### ⚡ Ultra-Fast Cloud Transfers & Uploads (New in v2.5.9)
- **Parallel Multi-File Transfers**: Dynamic `--transfers` setting (default: **4 parallel transfers**, configurable up to 8) replacing legacy single-file sequential bottlenecks.
- **Optimized Google Drive Chunk Size**: Upgraded from rclone's default 8 MB to **64 MB** (configurable up to 128 MB), slashing network round-trips by up to 16x and delivering **5x to 20x faster upload speeds**.
- **Atomic Upload Cutoff (`32M`)**: Files $\le 32\text{ MB}$ (photos, documents, standard crypt chunks) are uploaded in a single atomic multipart request with zero chunking session latency.
- **Tuned Google Drive Pacer**: Minimum sleep reduced from 100 ms to **10 ms** with a **200 burst** limit for near-instant directory lookups and file operations.
- **Configurable Performance Settings**: Full control available in **Settings > General > Transfers & Performance** (Transfer count, Chunk size, Fast-list toggle).

### 🚀 1-Click Google Drive Connection (One-Click Sign-In)
- Connect directly with your Google account in a single tap via Chrome Custom Tabs.
- No need to configure Google Cloud Developer Console, Client ID, Client Secret, or Service Accounts.
- Advanced settings remain available in a collapsible section for power users.

### 🔒 Interactive Crypt / Encrypted Remote Setup (New in v2.5.8)
- **Interactive Remote Selector**: Dropdown menu offering existing remotes (e.g., `Google Drive:`) with auto-completion.
- **Intelligent Target Auto-Repair**: Automatically detects missing colons, trims paths, handles subfolders, and fixes existing configs in the background.
- **Remote Drop Protection**: Remotes are never hidden or lost, even during fallback resolution.

### 📊 Accurate Real-time Progress & Percentages (New in v2.5.7)
- Real percentage tracking (**0% to 100%**) in Android notifications (fixes `NaN%` and frozen progress bars).
- Real-time display of transfer speed, transferred bytes, total size, and estimated time remaining (ETA).
- Isolated background task management (prevents false cancellations during update checks).

### 📱 Android 14 & 15 (API 35) Compatibility & Scoped Storage
- Compiled and targeted for **Android 15 (API 35)**.
- **Scoped Storage Protection**: Automatically excludes restricted system folders (`/Android/data/**`, `/Android/obb/**`) on Android 10+ with `--skip-links` to eliminate permission errors.
- **Foreground Service Compliance**: Correct `DATA_SYNC` foreground service types declared for Android 14+.
- Secure `RECEIVER_NOT_EXPORTED` broadcast receivers and runtime notification permission requests.

---

## 📦 Flavors: RS vs. OSS

| Feature | RS Flavor (Recommended) | OSS Flavor (Open Source) |
| :--- | :---: | :---: |
| **1-Click Google Drive Sign-In** | ✅ Built-in OAuth | ⚙️ Manual Client ID/Secret |
| **Encrypted Remotes (Crypt)** | ✅ Yes | ✅ Yes |
| **High-Performance Transfers (v2.5.9)** | ✅ Yes | ✅ Yes |
| **Streaming (FTP, HTTP, WebDAV, DLNA)** | ✅ Yes | ✅ Yes |
| **Task Management & Automation** | ✅ Yes | ✅ Yes |
| **All CPU Architectures** | ✅ Yes | ✅ Yes |

---

## 📥 Download & Installation

Signed release APKs are available in the [**Releases**](https://github.com/63Sylvain/Round-Sync/releases/latest) section or in the repository's `release-apks/v2.5.9/` folder:

| Architecture | Description | Target Devices |
| :--- | :--- | :--- |
| **ARM64-v8a** *(Recommended)* | 64-bit ARM APK (~32 MB) | ~99% of modern smartphones and tablets |
| **Universal** | Multi-ABI All-in-One APK (~136 MB) | Compatible with all Android devices |
| **ARMeabi-v7a** | 32-bit legacy ARM APK (~34 MB) | Older Android devices |
| **x86_64** | 64-bit Intel/AMD APK (~43 MB) | ChromeOS, PC emulators |
| **x86** | 32-bit Intel APK (~44 MB) | Legacy emulators |

---

## ⚙️ Transfers & Performance Settings

Configure your performance preferences under **Settings > General > Transfers & Performance**:

- **Simultaneous transfers**:
  - `1`: Battery saver mode / low memory.
  - `2`: Balanced mode.
  - `4 (Default)`: Recommended for high speed on Wi-Fi and 4G/5G.
  - `8`: Ultra-fast multi-threaded uploads.
- **Google Drive upload chunk size**:
  - `8 MB`: Minimal RAM footprint.
  - `16 MB` / `32 MB`: Moderate RAM usage.
  - `64 MB (Default)`: High throughput, best performance for fast networks.
  - `128 MB`: Maximum throughput for very large files on fast Wi-Fi / Fiber.
- **Fast directory listing (`--fast-list`)**:
  - Enabled by default for rapid recursive scanning with fewer API queries.

---

## 🛠️ Building from Source

### Prerequisites
- **JDK 17+**
- **Go 1.26.6+**
- **Android SDK** (API 35, Build-tools 35.0.0)
- **Android NDK** (27.3.13750724)

### Build Commands

```bash
# Clone the repository
git clone https://github.com/63Sylvain/Round-Sync.git
cd Round-Sync

# Build OSS flavor (Debug / Release)
./gradlew assembleOssDebug
./gradlew assembleOssRelease

# Build RS flavor with 1-Click Google Drive (Debug / Release)
./gradlew assembleRsDebug
./gradlew assembleRsRelease

# Run unit test suite
./gradlew :app:testOssDebugUnitTest -x :rclone:buildAll
```

Output APK files are located under `app/build/outputs/apk/`.

---

## 🤖 Automated CI / CD

Every push to `main` or `master` and every tag push triggers the [GitHub Actions Pipeline](https://github.com/63Sylvain/Round-Sync/actions):
- Compiles native Rclone binaries for 4 target architectures using Go.
- Builds all 10 release variants (`RS` and `OSS`).
- Signs all APKs using an RSA 2048-bit keystore (valid until 2054).
- Publishes artifacts and creates GitHub Releases.

---

## 📄 License & Credits

This project is licensed under the **GNU General Public License v3.0 (GPLv3)** - see [LICENSE](./LICENSE) for details.

- **Original Author**: Patryk Kaczmarkiewicz ([@patrykcoding](https://github.com/patrykcoding))
- **Past Maintainers**: Felix Nüsse ([@newhinton](https://github.com/newhinton)), x0b ([@x0b](https://github.com/x0b)), gulp79 ([@gulp79](https://github.com/gulp79))
- **Current Maintainer**: Sylvain ([@63Sylvain](https://github.com/63Sylvain))
- Powered by [Rclone](https://rclone.org)
