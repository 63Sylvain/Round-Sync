package ca.pkay.rcloneexplorer

import ca.pkay.rcloneexplorer.Items.RemoteItem
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CryptRemoteResolutionTest {

    private lateinit var rclone: Rclone

    @Before
    fun setUp() {
        rclone = Rclone(null)
    }


    @Test
    fun getRemoteType_withStandardColonTarget_resolvesUnderlyingType() {
        val remotesJSON = JSONObject().apply {
            put("Google Drive", JSONObject().apply {
                put("type", "drive")
                put("scope", "drive")
            })
            put("crypt", JSONObject().apply {
                put("type", "crypt")
                put("remote", "Google Drive:encrypted")
            })
        }

        val remoteItem = RemoteItem("crypt", "crypt")
        val result = rclone.getRemoteType(remotesJSON, remoteItem, "crypt", 8)

        assertNotNull(result)
        assertTrue(result.isCrypt)
        assertEquals("drive", result.typeReadable)
    }

    @Test
    fun getRemoteType_withoutColonTarget_resolvesUnderlyingType() {
        // User typed "Google Drive" without ":"
        val remotesJSON = JSONObject().apply {
            put("Google Drive", JSONObject().apply {
                put("type", "drive")
                put("scope", "drive")
            })
            put("crypt", JSONObject().apply {
                put("type", "crypt")
                put("remote", "Google Drive")
            })
        }

        val remoteItem = RemoteItem("crypt", "crypt")
        val result = rclone.getRemoteType(remotesJSON, remoteItem, "crypt", 8)

        assertNotNull(result)
        assertTrue(result.isCrypt)
        assertEquals("drive", result.typeReadable)
    }

    @Test
    fun getRemoteType_withCaseInsensitiveTarget_resolvesUnderlyingType() {
        // User typed "google drive:" or "google drive"
        val remotesJSON = JSONObject().apply {
            put("Google Drive", JSONObject().apply {
                put("type", "drive")
            })
            put("crypt", JSONObject().apply {
                put("type", "crypt")
                put("remote", "google drive:secret")
            })
        }

        val remoteItem = RemoteItem("crypt", "crypt")
        val result = rclone.getRemoteType(remotesJSON, remoteItem, "crypt", 8)

        assertNotNull(result)
        assertTrue(result.isCrypt)
        assertEquals("drive", result.typeReadable)
    }

    @Test
    fun getRemoteType_withSlashSubfolderWithoutColon_resolvesUnderlyingType() {
        // User typed "Google Drive/secret" instead of "Google Drive:secret"
        val remotesJSON = JSONObject().apply {
            put("Google Drive", JSONObject().apply {
                put("type", "drive")
            })
            put("crypt", JSONObject().apply {
                put("type", "crypt")
                put("remote", "Google Drive/secret")
            })
        }

        val remoteItem = RemoteItem("crypt", "crypt")
        val result = rclone.getRemoteType(remotesJSON, remoteItem, "crypt", 8)

        assertNotNull(result)
        assertTrue(result.isCrypt)
        assertEquals("drive", result.typeReadable)
    }

    @Test
    fun getRemoteType_withLocalPath_resolvesLocalType() {
        val remotesJSON = JSONObject().apply {
            put("crypt", JSONObject().apply {
                put("type", "crypt")
                put("remote", "/storage/emulated/0/encrypted")
            })
        }

        val remoteItem = RemoteItem("crypt", "crypt")
        val result = rclone.getRemoteType(remotesJSON, remoteItem, "crypt", 8)

        assertNotNull(result)
        assertTrue(result.isCrypt)
        assertEquals("local", result.typeReadable)
        assertTrue(result.isPathAlias)
    }

    @Test
    fun getRemoteType_withNonExistentTarget_neverReturnsNull() {
        val remotesJSON = JSONObject().apply {
            put("crypt", JSONObject().apply {
                put("type", "crypt")
                put("remote", "nonexistent:folder")
            })
        }

        val remoteItem = RemoteItem("crypt", "crypt")
        val result = rclone.getRemoteType(remotesJSON, remoteItem, "crypt", 8)

        // Must never return null!
        assertNotNull(result)
        assertTrue(result.isCrypt)
        assertEquals("crypt", result.name)
    }

    @Test
    fun getRemoteType_withEmptyRemoteTarget_neverReturnsNull() {
        val remotesJSON = JSONObject().apply {
            put("crypt", JSONObject().apply {
                put("type", "crypt")
                put("remote", "")
            })
        }

        val remoteItem = RemoteItem("crypt", "crypt")
        val result = rclone.getRemoteType(remotesJSON, remoteItem, "crypt", 8)

        assertNotNull(result)
        assertTrue(result.isCrypt)
        assertEquals("crypt", result.name)
    }
}
