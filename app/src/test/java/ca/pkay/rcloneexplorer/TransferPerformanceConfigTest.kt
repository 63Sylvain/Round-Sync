package ca.pkay.rcloneexplorer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TransferPerformanceConfigTest {

    private lateinit var rclone: Rclone

    @Before
    fun setUp() {
        rclone = Rclone(null)
    }

    @Test
    fun defaultPerformanceParameters_containsHighPerformanceDefaults() {
        val params = rclone.getPerformanceParameters(true)

        // Check transfers flag and value (default 4 instead of 1)
        val transfersIndex = params.indexOf("--transfers")
        assertTrue("Params should contain --transfers", transfersIndex >= 0)
        assertEquals("Default transfers should be 4", "4", params[transfersIndex + 1])

        // Check drive chunk size (default 64M instead of 8M)
        val chunkSizeIndex = params.indexOf("--drive-chunk-size")
        assertTrue("Params should contain --drive-chunk-size", chunkSizeIndex >= 0)
        assertEquals("Default chunk size should be 64M", "64M", params[chunkSizeIndex + 1])

        // Check drive upload cutoff (32M to avoid chunk overhead on smaller files)
        val cutoffIndex = params.indexOf("--drive-upload-cutoff")
        assertTrue("Params should contain --drive-upload-cutoff", cutoffIndex >= 0)
        assertEquals("Default upload cutoff should be 32M", "32M", params[cutoffIndex + 1])

        // Check drive pacer tuning
        val pacerSleepIndex = params.indexOf("--drive-pacer-min-sleep")
        assertTrue("Params should contain --drive-pacer-min-sleep", pacerSleepIndex >= 0)
        assertEquals("Pacer sleep should be 10ms", "10ms", params[pacerSleepIndex + 1])

        val pacerBurstIndex = params.indexOf("--drive-pacer-burst")
        assertTrue("Params should contain --drive-pacer-burst", pacerBurstIndex >= 0)
        assertEquals("Pacer burst should be 200", "200", params[pacerBurstIndex + 1])

        // Check buffer size
        val bufferIndex = params.indexOf("--buffer-size")
        assertTrue("Params should contain --buffer-size", bufferIndex >= 0)
        assertEquals("Buffer size should be 16M", "16M", params[bufferIndex + 1])

        // Check checkers
        val checkersIndex = params.indexOf("--checkers")
        assertTrue("Params should contain --checkers", checkersIndex >= 0)
        assertEquals("Checkers should be 8", "8", params[checkersIndex + 1])
    }

    @Test
    fun fastList_includedForDirectoryOperations() {
        val dirParams = rclone.getPerformanceParameters(true)
        assertTrue("Directory operations should include --fast-list", dirParams.contains("--fast-list"))
    }

    @Test
    fun fastList_excludedForSingleFileOperations() {
        val fileParams = rclone.getPerformanceParameters(false)
        assertFalse("Single file operations should not include --fast-list", fileParams.contains("--fast-list"))
    }

    @Test
    fun performanceParameters_hasEvenNumberOfKeyValuesExceptFastList() {
        val fileParams = rclone.getPerformanceParameters(false)
        // Every flag has an argument: --transfers 4, --drive-chunk-size 64M, etc.
        assertEquals("Parameters for single file should all be key-value pairs", 0, fileParams.size % 2)
    }
}
