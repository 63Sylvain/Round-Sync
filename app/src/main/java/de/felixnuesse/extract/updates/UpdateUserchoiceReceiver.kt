package de.felixnuesse.extract.updates

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager
import ca.pkay.rcloneexplorer.BuildConfig
import ca.pkay.rcloneexplorer.R
import de.felixnuesse.extract.extensions.tag
import de.felixnuesse.extract.notifications.AppUpdateNotification
import java.io.File
import java.io.FileOutputStream
import java.net.URL


class UpdateUserchoiceReceiver : BroadcastReceiver() {

    companion object {
        var ACTION_IGNORE = "ACTION_IGNORE"
        var ACTION_DOWNLOAD = "ACTION_DOWNLOAD"
        var IGNORE_VERSION_EXTRA = "IGNORE_VERSION_EXTRA"
    }
    override fun onReceive(context: Context, intent: Intent) {

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        if(intent.action == ACTION_IGNORE) {
            Log.e(tag(), "Ignore current update!")
            val key = context.getString(R.string.pref_key_app_update_dismiss_current_update)
            preferenceManager.edit().putString(key, intent.getStringExtra(IGNORE_VERSION_EXTRA)).apply()
            AppUpdateNotification(context).cancelNotification()
        }

        if(intent.action == ACTION_DOWNLOAD) {
            val versionKey = context.getString(R.string.pref_key_app_updates_found_update_for_version)
            val version = preferenceManager.getString(versionKey,"")?: ""

            // the following might be superfluous. keep it for universal fallback.
            val primaryAbi = if (Build.SUPPORTED_ABIS.isNotEmpty()) Build.SUPPORTED_ABIS[0] else "universal"
            val abi = when(primaryAbi) {
                "x86" -> "x86"
                "x86_64" -> "x86_64"
                "arm64-v8a" -> "arm64-v8a"
                "armeabi-v7a" -> "armeabi-v7a"
                else -> {
                    Log.e(tag(), "Unknown ABI, trying universal!")
                    "universal"
                }
            }

            if(version.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
                    val pendingResult = goAsync()
                    downloadAndInstall(
                        URL("https://github.com/newhinton/Round-Sync/releases/download/$version/roundsync_$version-oss-$abi-release.apk"),
                        context,
                        version,
                        abi,
                        pendingResult
                    )
                }
            }
            AppUpdateNotification(context).cancelNotification()
        }
    }

    @RequiresApi(VERSION_CODES.N)
    private fun downloadAndInstall(url: URL, context: Context, version: String, abi: String, pendingResult: PendingResult) {
        Log.e(tag(), "Download url: $url")
        Thread {
            try {
                val dir = context.externalCacheDir?.absolutePath ?: ""
                Log.e(tag(), "Download dir: $dir")
                val target = File(dir, "roundsync_$version-oss-$abi-release.apk")
                url.openStream()
                    .use { input ->
                    FileOutputStream(target).use {
                        input.copyTo(it)
                    }
                }

                val fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", target)

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e(tag(), "Failed to download update: ${e.message}", e)
            } finally {
                pendingResult.finish()
            }
        }.start()
    }
}