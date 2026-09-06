package ca.pkay.rcloneexplorer.notifications.support

import android.content.Context
import android.text.format.Formatter
import android.util.Log
import ca.pkay.rcloneexplorer.R
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class StatusObject(var mContext: Context){

    private val TAG = "StatusObject"
    var notificationPercent: Int = 0
    var notificationContent: String = ""
    var notificationBigText = ArrayList<String>()
    var mErrorList = ArrayList<ErrorObject>()
    var mStats = JSONObject()
    var mLogline = JSONObject()

    var estimatedAverageSpeed = 0L
    var lastItemAverageSpeed = 0L

    fun getSpeed(): String {
        val formatted = try { Formatter.formatFileSize(mContext, mStats.optLong("speed", 0)) } catch (e: Exception) { null }
        return (formatted ?: "${mStats.optLong("speed", 0)} B") + "/s"
    }

    /**
     * This function is off by a bit. Afaik rclone calculates the average speed per file,
     * while we calculate the average speed overall. This means this estimate is likely a bit lower
     * than the real world speeds.
     * It also includes the time rclone requires to check the file after transfer, so it does not
     * reflect actual network speeds.
     */
    fun getEstimatedAverageSpeed(): String {
        val formatted = try { Formatter.formatFileSize(mContext, estimatedAverageSpeed) } catch (e: Exception) { null }
        return (formatted ?: "$estimatedAverageSpeed B") + "/s"
    }

    fun getLastItemAverageSpeed(): String {
        val formatted = try { Formatter.formatFileSize(mContext, lastItemAverageSpeed) } catch (e: Exception) { null }
        return (formatted ?: "$lastItemAverageSpeed B") + "/s"
    }

    fun getSize(): String {
        val formatted = try { Formatter.formatFileSize(mContext, mStats.optLong("bytes", 0)) } catch (e: Exception) { null }
        return formatted ?: "${mStats.optLong("bytes", 0)} B"
    }

    fun getTotalSize(): String {
        val formatted = try { Formatter.formatFileSize(mContext, mStats.optLong("totalBytes", 0)) } catch (e: Exception) { null }
        return formatted ?: "${mStats.optLong("totalBytes", 0)} B"
    }

    fun getPercentage(): Double {
        if (mStats.has("percentage")) {
            val p = mStats.optDouble("percentage", 0.0)
            if (!p.isNaN() && !p.isInfinite()) {
                return p.coerceIn(0.0, 100.0)
            }
        }
        val totalBytes = mStats.optLong("totalBytes", 0)
        if (totalBytes > 0) {
            val bytes = mStats.optLong("bytes", 0)
            val pct = (bytes.toDouble() / totalBytes.toDouble()) * 100.0
            return if (pct.isNaN() || pct.isInfinite()) 0.0 else pct.coerceIn(0.0, 100.0)
        }
        val totalTransfers = mStats.optInt("totalTransfers", 0)
        if (totalTransfers > 0) {
            val transfers = mStats.optInt("transfers", 0)
            val pct = (transfers.toDouble() / totalTransfers.toDouble()) * 100.0
            return if (pct.isNaN() || pct.isInfinite()) 0.0 else pct.coerceIn(0.0, 100.0)
        }
        return 0.0
    }

    fun getTransfers(): Int {
        return mStats.optInt("transfers", 0)
    }

    fun getTotalTransfers(): Int {
        return mStats.optInt("totalTransfers", 0)
    }

    fun getDeletions(): Int {
        return mStats.optInt("deletes", 0) + mStats.optInt("deletedDirs", 0)
    }

    fun getErrorMessage(): String {
        if(mLogline.has("msg") && mLogline.optString("level") == "error") {
            return mLogline.optString("msg", "")
        }
        return ""
    }

    fun getErrorObject(): String {
        if(mLogline.has("msg") && mLogline.optString("level") == "error") {
            return mLogline.optString("object", "")
        }
        return ""
    }

    fun parseLoglineToStatusObject(logLine: JSONObject) {
        if (logLine.optString("level") == "error") {
            mLogline = logLine
            val error = ErrorObject(getErrorObject(), getErrorMessage())
            Log.e(TAG, "${error.mErrorObject} - ${error.mErrorMessage}")
            mErrorList.add(error)
        }

        if (logLine.has("stats")) {
            clearObject()
            mLogline = logLine
            mStats = mLogline.getJSONObject("stats")

            // Available stats:
            // bytes, checks, deletedDirs, deletes, elapsedTime, errors, eta, fatalError, renames, retryError
            // speed, totalBytes, totalChecks, totalTransfers, transferTime, transfers

            val checks = mStats.optJSONArray("checking")
            if (checks != null && checks.length() > 0) {
                val filename = checks.optString(0, "")
                if (filename.isNotEmpty()) {
                    notificationBigText.add(
                        String.format(
                            mContext.getString(R.string.sync_notification_elapsed),
                            prettyPrintDuration(mStats.optInt("elapsedTime", 0))
                        )
                    )
                    notificationBigText.add(
                        String.format(
                            mContext.getString(R.string.sync_notification_file_checking),
                            filename
                        )
                    )
                    notificationContent = String.format(
                        mContext.getString(R.string.sync_notification_file_checking),
                        filename
                    )
                    notificationPercent = 0
                    return
                }
            }

            val transferringArray = mStats.optJSONArray("transferring")
            if (transferringArray != null && transferringArray.length() > 0) {
                val transferring = transferringArray.optJSONObject(0)
                if (transferring != null) {
                    lastItemAverageSpeed = transferring.optLong("speedAvg", 0)
                    val divisor = mStats.optInt("elapsedTime", 0)
                    estimatedAverageSpeed = if (divisor != 0) {
                        mStats.optLong("bytes", 0) / divisor
                    } else {
                        0
                    }
                }
            }

            val speed = getSpeed()
            val size = getSize()
            val allsize = getTotalSize()
            val percent: Double = getPercentage()
            notificationPercent = percent.toInt().coerceIn(0, 100)

            val percentStr = "${notificationPercent}%"
            val baseShortContent = try {
                String.format(
                    mContext.getString(R.string.sync_notification_short),
                    size,
                    allsize,
                    prettyPrintDuration(mStats.optInt("eta", 0))
                )
            } catch (e: Exception) {
                "$size / $allsize"
            }
            notificationContent = "$percentStr • $baseShortContent"

            try {
                notificationBigText.clear()
                notificationBigText.add(
                    String.format(
                        mContext.getString(R.string.sync_notification_transferred),
                        size,
                        allsize
                    ) + " ($percentStr)"
                )

                if (getDeletions() > 0) {
                    notificationBigText.add(
                        String.format(
                            mContext.getString(R.string.sync_notification_deletions),
                            getDeletions()
                        )
                    )
                }

                notificationBigText.add(
                    String.format(
                        mContext.getString(R.string.sync_notification_speed),
                        speed
                    )
                )

                val etaSeconds = mStats.optInt("eta", 0)
                notificationBigText.add(
                    String.format(
                        mContext.getString(R.string.sync_notification_remaining),
                        prettyPrintDuration(etaSeconds)
                    )
                )

                if (mStats.optInt("errors", 0) > 0) {
                    notificationBigText.add(
                        String.format(
                            mContext.getString(R.string.sync_notification_errors),
                            mStats.optInt("errors", 0)
                        )
                    )
                }

                notificationBigText.add(
                    String.format(
                        mContext.getString(R.string.sync_notification_elapsed),
                        prettyPrintDuration(mStats.optInt("elapsedTime", 0))
                    )
                )

                if (transferringArray != null && transferringArray.length() > 0) {
                    val transferObject = transferringArray.optJSONObject(0)
                    val filename = transferObject?.optString("name", "") ?: ""
                    if (filename.isNotEmpty()) {
                        notificationBigText.add(
                            String.format(
                                mContext.getString(R.string.sync_notification_file_syncing),
                                filename
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // If context resources are unavailable (e.g. unit test), fallback gracefully
            }
        }
    }

    fun clearObject() {
        notificationPercent = 0
        notificationContent = ""
        notificationBigText = ArrayList<String>()
        mLogline = JSONObject()
    }

    fun printErrors(){
        mErrorList.forEach {
            Log.e(TAG, it.mErrorObject + " - " + it.mErrorMessage)
        }
    }

    fun getAllErrorMessages(): String{
        var all = ""
        mErrorList.forEach {
            all += it.mErrorMessage + "\n"
            all += mContext.getString(R.string.status_offendingfile) + it.mErrorObject + "\n"
        }
        return all
    }

    override fun toString(): String {
        val pct = getPercentage()
        val pctFormatted = if (pct.isNaN()) "0%" else String.format(java.util.Locale.US, "%.1f%%", pct)
        val errorMsg = getErrorMessage()
        val errPart = if (errorMsg.isNotEmpty()) " - Error: $errorMsg" else ""
        return "Progress: $pctFormatted ($notificationPercent%), Transferred: ${getSize()} / ${getTotalSize()}, Speed: ${getSpeed()}$errPart"
    }


    private fun prettyPrintDuration(secondDuration: Int) : String {

        var duration = secondDuration.toLong()
        val days = TimeUnit.SECONDS.toDays(duration).toInt()
        duration -= (days * 60 * 60 * 24)
        val hours = TimeUnit.SECONDS.toHours(duration).toInt()
        duration -= (hours * 60 * 60)
        val minutes = TimeUnit.SECONDS.toMinutes(duration).toInt()
        duration -= (minutes * 60)
        val seconds = TimeUnit.SECONDS.toSeconds(duration).toInt()

        var daysText = String.format(
            mContext.resources.getQuantityString(R.plurals.modern_prettyprint_duration_d,
                days,
                days
            )
        )

        var hoursText = String.format(
            mContext.resources.getQuantityString(R.plurals.modern_prettyprint_duration_h,
                hours,
                hours
            )
        )

        var minutesText = String.format(
            mContext.resources.getQuantityString(R.plurals.modern_prettyprint_duration_m,
                minutes,
                minutes
            )
        )

        var secondsText = String.format(
            mContext.resources.getQuantityString(R.plurals.modern_prettyprint_duration_s,
                seconds,
                seconds
            )
        )

        if (days > 0) {
            daysText = "$daysText, "
        } else {
            daysText = ""
        }

        if (hours > 0) {
            hoursText = "$hoursText, "
        } else {
            hoursText = ""
        }

        if (minutes > 0) {
            minutesText = "$minutesText, "
        } else {
            minutesText = ""
        }

        //if the time is longer than an hour, dont show seconds
        return if (60*60 < secondDuration) {
            "$daysText$hoursText$minutesText"
        } else {
            "$daysText$hoursText$minutesText$secondsText"
        }
    }
}