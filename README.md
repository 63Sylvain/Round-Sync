# Round Sync - Rclone for Android
[![license: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://github.com/63Sylvain/Round-Sync/blob/main/LICENSE)
[![GitHub release](https://img.shields.io/github/v/release/63Sylvain/Round-Sync?include_prereleases)](https://github.com/63Sylvain/Round-Sync/releases/latest)
![Latest Downloads](https://img.shields.io/github/downloads/63Sylvain/Round-Sync/total)

> ⚠️ **Unofficial Fork Disclaimer**  
> This is an **unofficial fork** of [Round Sync - Rclone for Android](https://github.com/gulp79/Round-Sync), which is itself a fork of [Round Sync - Rclone for Android](https://github.com/newhinton/Round-Sync), with additional enhancements.
> All the backends are from [rclone-extra](https://github.com/gulp79/rclone-extra).
> I am **not affiliated with the upstream maintainers**, and this fork **does not intend to be malicious or harmful** in any way.  
> Please **read the source code** if you're unsure or want to verify that it behaves as described.  
> Contributions, feedback, and scrutiny are welcome.

A cloud file manager, powered by rclone.
Visit [https://roundsync.com](https://roundsync.com) for more information!

## Features
- **File Management** (list, view, download, upload, move, rename, delete files and folders)
- **Streaming** (Stream media files, serve files and directories over FTP, HTTP, WebDAV or DLNA)
- **Integration** (Access local storage devices and share files with the application to store them on a remote)
- **Many cloud storage providers** (all via rclone config import, some without ui-setup)
- **Material 3 Design** (Dark theme)
- **All architectures** (runs on ARM, ARM64, x86 and x64 devices, Android 7+)
- **Storage Access Framework (SAF)**
- **Intentservice** to start tasks via third party apps!
- **Task Management** to allow regular runs of your important tasks!

## Installation

Grab the [latest version](https://github.com/63Sylvain/Round-Sync/releases/latest) of the signed APK and install it on your phone.

## Building from Source

### Prerequisites
- Java 17+
- Go 1.20+
- Android SDK
- Android NDK (auto-downloaded)

### Build Commands

```bash
# Debug build
./gradlew assembleOssDebug

# Release build
./gradlew assembleOssRelease
```

The APK files will be in `app/build/outputs/apk/`

## Automated Builds

This repository includes GitHub Actions workflows that automatically:
- Build APK in release mode on every push
- Sign the APK with a secure keystore
- Upload artifacts for download
- Create releases on tag pushes

Check the [Actions](https://github.com/63Sylvain/Round-Sync/actions) tab for build status.

## License
GPLv3 - See [LICENSE](./LICENSE) for details

## About
This is a fork of [Round Sync by gulp79](https://github.com/gulp79/Round-Sync)
