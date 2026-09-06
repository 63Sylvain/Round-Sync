package ca.pkay.rcloneexplorer

import ca.pkay.rcloneexplorer.notifications.support.StatusObject
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StatusObjectTest {

    private lateinit var statusObject: StatusObject

    @Before
    fun setUp() {
        val dummyContext = object : android.content.ContextWrapper(null) {}
        statusObject = StatusObject(dummyContext)
    }

    @Test
    fun getPercentage_returnsZeroWhenTotalBytesZeroAndNoTransfers() {
        val pct = statusObject.getPercentage()
        assertFalse("Percentage should never be NaN", pct.isNaN())
        assertFalse("Percentage should never be infinite", pct.isInfinite())
        assertEquals(0.0, pct, 0.001)
    }

    @Test
    fun getPercentage_returnsCorrectPercentageFromBytes() {
        val stats = JSONObject().apply {
            put("bytes", 50L)
            put("totalBytes", 200L)
        }
        statusObject.mStats = stats
        val pct = statusObject.getPercentage()
        assertFalse("Percentage should not be NaN", pct.isNaN())
        assertEquals(25.0, pct, 0.001)
    }

    @Test
    fun getPercentage_returnsCorrectPercentageFromRclonePercentageField() {
        val stats = JSONObject().apply {
            put("percentage", 42.5)
            put("bytes", 0L)
            put("totalBytes", 0L)
        }
        statusObject.mStats = stats
        val pct = statusObject.getPercentage()
        assertFalse("Percentage should not be NaN", pct.isNaN())
        assertEquals(42.5, pct, 0.001)
    }

    @Test
    fun getPercentage_fallbackToTransfersWhenTotalBytesZero() {
        val stats = JSONObject().apply {
            put("bytes", 0L)
            put("totalBytes", 0L)
            put("transfers", 2)
            put("totalTransfers", 8)
        }
        statusObject.mStats = stats
        val pct = statusObject.getPercentage()
        assertFalse("Percentage should not be NaN", pct.isNaN())
        assertEquals(25.0, pct, 0.001)
    }

    @Test
    fun parseLoglineToStatusObject_handlesEmptyArraysWithoutException() {
        val stats = JSONObject().apply {
            put("bytes", 100L)
            put("totalBytes", 400L)
            put("checking", JSONArray())
            put("transferring", JSONArray())
            put("elapsedTime", 5)
            put("eta", 15)
            put("speed", 1024L)
        }
        val logLine = JSONObject().apply {
            put("level", "info")
            put("stats", stats)
        }

        // Must not throw JSONException: Index 0 out of range
        statusObject.parseLoglineToStatusObject(logLine)
        assertEquals(25, statusObject.notificationPercent)
    }

    @Test
    fun toString_doesNotContainNaN() {
        val str = statusObject.toString()
        assertFalse("toString should not contain NaN", str.contains("NaN"))
        assertTrue("toString should contain Progress:", str.contains("Progress:"))
    }
}