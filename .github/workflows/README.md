# GitHub Actions Workflows

## Build APK Release (`build-apk-release.yml`)

**Triggers:**
- Every push to `main`, `master`, or `develop` branches
- Pull requests to `main` or `master`
- Manual trigger (workflow_dispatch)

**What it does:**
1. ✅ Sets up Java 17 & Go 1.26.6
2. ✅ Installs Android SDK & NDK
3. ✅ Generates a secure keystore for signing
4. ✅ Builds APK in release mode (OSS flavor)
5. ✅ Uploads APK as artifact (30 days retention)
6. ✅ Creates GitHub Release on tag push

**Output:**
- APK files in `app/build/outputs/apk/oss/release/`
- Available as artifacts in the Actions run
- Auto-released when you push a tag

## How to Use

### Automatic builds (every push):
Just push to main/master and check the Actions tab!

### Create a Release:
```bash
git tag -a v2.5.6 -m "Release version 2.5.6"
git push origin v2.5.6
```

The workflow will:
1. Build the APK
2. Create a GitHub Release
3. Upload the APK automatically

## Download APKs

1. Go to [Releases](https://github.com/63Sylvain/Round-Sync/releases)
2. Or check [Actions](https://github.com/63Sylvain/Round-Sync/actions) → latest run → Artifacts
