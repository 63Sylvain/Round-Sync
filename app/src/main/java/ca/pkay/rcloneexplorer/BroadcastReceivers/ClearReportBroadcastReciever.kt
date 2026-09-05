package ca.pkay.rcloneexplorer.BroadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.edit
import ca.pkay.rcloneexplorer.notifications.ReportNotifications
import ca.pkay.rcloneexplorer.notifications.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ClearReportBroadcastReciever: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action ?: return
        if (context == null) return

        if (action == ReportNotifications.REPORT_SUCCESS_DELETE_INTENT || action == ReportNotifications.REPORT_FAIL_DELETE_INTENT) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    context.dataStore.edit { settings ->
                        if (action == ReportNotifications.REPORT_SUCCESS_DELETE_INTENT) {
                            settings[ReportNotifications.NOTIFICATION_CACHE_SUCCESS_PREFERENCE] = ""
                        } else if (action == ReportNotifications.REPORT_FAIL_DELETE_INTENT) {
                            settings[ReportNotifications.NOTIFICATION_CACHE_FAIL_PREFERENCE] = ""
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}