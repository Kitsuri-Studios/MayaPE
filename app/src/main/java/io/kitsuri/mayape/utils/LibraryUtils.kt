package io.kitsuri.mayape.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Environment
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

object LibraryUtils {
    private const val TAG = "LibraryUtils"
    private const val MODULES_DIR = "modules"
    private const val HXO_INI = "HXO.ini"
    private const val MODULES_JSON = "modules.json"
    private const val PREFS_NAME = "mod_preferences"
    private const val PREFS_MOD_PREFIX = "mod_"

    // Formatted JSON instance for pretty printing
    private val prettyJson = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    /**
     * Gets the media directory for the app: /Android/media/APP_ID/
     */
    private fun getMediaDirectory(context: Context): File? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getExternalMediaDirs().firstOrNull()
        } else {
            val externalStorageState = Environment.getExternalStorageState()
            if (Environment.MEDIA_MOUNTED == externalStorageState) {
                val mediaDir = File(
                    Environment.getExternalStorageDirectory(),
                    "Android/media/${context.packageName}"
                )
                mediaDir
            } else {
                null
            }
        }
    }

    /**
     * Gets shared preferences for mod states
     */
    private fun getModPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Save mod state to shared preferences
     */
    private fun saveModState(context: Context, fileName: String, enabled: Boolean) {
        val prefs = getModPreferences(context)
        prefs.edit().putBoolean(PREFS_MOD_PREFIX + fileName, enabled).apply()
        Log.d(TAG, "Saved mod state: $fileName = $enabled")
    }

    /**
     * Get mod state from shared preferences
     */
    private fun getModState(context: Context, fileName: String, defaultValue: Boolean = true): Boolean {
        val prefs = getModPreferences(context)
        return prefs.getBoolean(PREFS_MOD_PREFIX + fileName, defaultValue)
    }

    /**
     * Remove mod state from shared preferences
     */
    private fun removeModState(context: Context, fileName: String) {
        val prefs = getModPreferences(context)
        prefs.edit().remove(PREFS_MOD_PREFIX + fileName).apply()
        Log.d(TAG, "Removed mod state from preferences: $fileName")
    }

    fun initializeFiles(context: Context): Boolean {
        return try {
            val mediaDir = getMediaDirectory(context)
            if (mediaDir == null) {
                Log.e(TAG, "Media directory is not available")
                return false
            }

            Log.d(TAG, "Using media directory: ${mediaDir.absolutePath}")

            // Ensure media directory exists
            if (!mediaDir.exists()) {
                val created = mediaDir.mkdirs()
                Log.d(TAG, "Media directory created: $created")
                if (!created) {
                    Log.e(TAG, "Failed to create media directory")
                    return false
                }
            }

            // Create modules directory
            val modulesDir = File(mediaDir, MODULES_DIR)
            Log.d(TAG, "Modules directory path: ${modulesDir.absolutePath}")

            if (!modulesDir.exists()) {
                val created = modulesDir.mkdirs()
                Log.d(TAG, "Modules directory created: $created")
                if (!created) {
                    Log.e(TAG, "Failed to create modules directory")
                    return false
                }
            } else {
                Log.d(TAG, "Modules directory already exists")
            }

            // Copy HXO.ini from assets
            val hxoIniFile = File(mediaDir, HXO_INI)
            Log.d(TAG, "HXO.ini file path: ${hxoIniFile.absolutePath}")

            if (!hxoIniFile.exists()) {
                Log.d(TAG, "HXO.ini doesn't exist, attempting to copy from assets")

                try {
                    // Check if asset exists first
                    val assetList = context.assets.list("") ?: emptyArray()
                    Log.d(TAG, "Available assets: ${assetList.joinToString()}")

                    if (!assetList.contains(HXO_INI)) {
                        Log.e(TAG, "HXO.ini not found in assets")
                        return false
                    }

                    context.assets.open(HXO_INI).use { input ->
                        hxoIniFile.outputStream().use { output ->
                            val bytesTransferred = input.copyTo(output)
                            Log.d(TAG, "HXO.ini copied successfully, bytes: $bytesTransferred")
                        }
                    }

                    if (!hxoIniFile.exists()) {
                        Log.e(TAG, "HXO.ini file was not created after copying")
                        return false
                    }

                } catch (e: IOException) {
                    Log.e(TAG, "IOException while copying HXO.ini", e)
                    return false
                } catch (e: Exception) {
                    Log.e(TAG, "Unexpected error while copying HXO.ini", e)
                    return false
                }
            } else {
                Log.d(TAG, "HXO.ini already exists")
            }

            // Initialize modules.json
            val modulesJsonFile = File(mediaDir, MODULES_JSON)
            Log.d(TAG, "modules.json file path: ${modulesJsonFile.absolutePath}")

            if (!modulesJsonFile.exists()) {
                try {
                    val emptyJson = prettyJson.encodeToString(emptyMap<String, Boolean>())
                    modulesJsonFile.writeText(emptyJson)
                    Log.d(TAG, "modules.json created successfully")

                    if (!modulesJsonFile.exists()) {
                        Log.e(TAG, "modules.json was not created after writing")
                        return false
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error creating modules.json", e)
                    return false
                }
            } else {
                Log.d(TAG, "modules.json already exists")
            }

            Log.d(TAG, "File initialization completed successfully in media directory")
            true

        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during file initialization", e)
            false
        }
    }

    fun copyModFile(context: Context, sourceFile: File, fileName: String): File? {
        return try {
            val mediaDir = getMediaDirectory(context)
            if (mediaDir == null) {
                Log.e(TAG, "Media directory is not available")
                return null
            }

            val modulesDir = File(mediaDir, MODULES_DIR)

            if (!modulesDir.exists()) {
                Log.w(TAG, "Modules directory doesn't exist, creating it")
                if (!modulesDir.mkdirs()) {
                    Log.e(TAG, "Failed to create modules directory")
                    return null
                }
            }

            val destFile = File(modulesDir, fileName)
            Log.d(TAG, "Copying mod file from ${sourceFile.absolutePath} to ${destFile.absolutePath}")

            sourceFile.copyTo(destFile, overwrite = true)

            if (!destFile.exists()) {
                Log.e(TAG, "Destination file was not created after copying")
                return null
            }

            Log.d(TAG, "Mod file copied successfully to media directory")
            destFile
        } catch (e: Exception) {
            Log.e(TAG, "Error copying mod file", e)
            null
        }
    }

    fun updateModulesJson(context: Context, fileName: String): Boolean {
        return try {
            val mediaDir = getMediaDirectory(context)
            if (mediaDir == null) {
                Log.e(TAG, "Media directory is not available")
                return false
            }

            val modulesJsonFile = File(mediaDir, MODULES_JSON)
            Log.d(TAG, "Updating modules.json with file: $fileName")

            val modulesJson = if (modulesJsonFile.exists()) {
                try {
                    prettyJson.decodeFromString<Map<String, Boolean>>(modulesJsonFile.readText())
                } catch (e: Exception) {
                    Log.w(TAG, "Error reading existing modules.json, using empty map", e)
                    emptyMap()
                }
            } else {
                Log.w(TAG, "modules.json doesn't exist, using empty map")
                emptyMap()
            }

            // Get previous state from shared preferences, default to true
            val previousState = getModState(context, fileName, true)

            val updatedModules = modulesJson.toMutableMap().apply {
                put(fileName, previousState)
            }

            val jsonString = prettyJson.encodeToString(updatedModules)
            modulesJsonFile.writeText(jsonString)

            // Save to shared preferences
            saveModState(context, fileName, previousState)

            Log.d(TAG, "modules.json updated successfully in media directory")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating modules.json", e)
            false
        }
    }

    /**
     * Enable a mod
     */
    fun enableMod(context: Context, fileName: String): Boolean {
        return toggleMod(context, fileName, true)
    }

    /**
     * Disable a mod
     */
    fun disableMod(context: Context, fileName: String): Boolean {
        return toggleMod(context, fileName, false)
    }

    /**
     * Toggle mod state
     */
    private fun toggleMod(context: Context, fileName: String, enabled: Boolean): Boolean {
        return try {
            val mediaDir = getMediaDirectory(context)
            if (mediaDir == null) {
                Log.e(TAG, "Media directory is not available")
                return false
            }

            val modulesJsonFile = File(mediaDir, MODULES_JSON)

            val modulesJson = if (modulesJsonFile.exists()) {
                try {
                    prettyJson.decodeFromString<Map<String, Boolean>>(modulesJsonFile.readText())
                } catch (e: Exception) {
                    Log.w(TAG, "Error reading existing modules.json", e)
                    return false
                }
            } else {
                Log.w(TAG, "modules.json doesn't exist")
                return false
            }

            if (!modulesJson.containsKey(fileName)) {
                Log.w(TAG, "Mod $fileName not found in modules.json")
                return false
            }

            val updatedModules = modulesJson.toMutableMap().apply {
                put(fileName, enabled)
            }

            val jsonString = prettyJson.encodeToString(updatedModules)
            modulesJsonFile.writeText(jsonString)

            // Save to shared preferences
            saveModState(context, fileName, enabled)

            Log.d(TAG, "Mod $fileName ${if (enabled) "enabled" else "disabled"}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling mod", e)
            false
        }
    }

    /**
     * Remove/delete a mod
     */
    fun removeMod(context: Context, fileName: String): Boolean {
        return try {
            val mediaDir = getMediaDirectory(context)
            if (mediaDir == null) {
                Log.e(TAG, "Media directory is not available")
                return false
            }

            // Remove from modules directory
            val modulesDir = File(mediaDir, MODULES_DIR)
            val modFile = File(modulesDir, fileName)
            if (modFile.exists()) {
                val deleted = modFile.delete()
                Log.d(TAG, "Mod file $fileName deleted: $deleted")
            }

            // Remove from modules.json
            val modulesJsonFile = File(mediaDir, MODULES_JSON)
            val modulesJson = if (modulesJsonFile.exists()) {
                try {
                    prettyJson.decodeFromString<Map<String, Boolean>>(modulesJsonFile.readText())
                } catch (e: Exception) {
                    Log.w(TAG, "Error reading existing modules.json", e)
                    return false
                }
            } else {
                emptyMap()
            }

            val updatedModules = modulesJson.toMutableMap().apply {
                remove(fileName)
            }

            val jsonString = prettyJson.encodeToString(updatedModules)
            modulesJsonFile.writeText(jsonString)

            // Remove from shared preferences
            removeModState(context, fileName)

            Log.d(TAG, "Mod $fileName removed from modules.json and preferences")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error removing mod", e)
            false
        }
    }

    /**
     * Refresh mods - scan modules directory and update JSON file
     * This will add newly added files and remove deleted files
     */
    fun refreshMods(context: Context): Boolean {
        return try {
            val mediaDir = getMediaDirectory(context)
            if (mediaDir == null) {
                Log.e(TAG, "Media directory is not available")
                return false
            }

            val modulesDir = File(mediaDir, MODULES_DIR)
            if (!modulesDir.exists()) {
                Log.w(TAG, "Modules directory doesn't exist")
                return false
            }

            // Get all .so and .hxo files from modules directory
            val actualModFiles = modulesDir.listFiles { file ->
                file.isFile && (file.name.endsWith(".so") || file.name.endsWith(".hxo"))
            }?.map { it.name }?.toSet() ?: emptySet()

            Log.d(TAG, "Found mod files in directory: $actualModFiles")

            // Read current modules.json
            val modulesJsonFile = File(mediaDir, MODULES_JSON)
            val currentModulesJson = if (modulesJsonFile.exists()) {
                try {
                    prettyJson.decodeFromString<Map<String, Boolean>>(modulesJsonFile.readText())
                } catch (e: Exception) {
                    Log.w(TAG, "Error reading existing modules.json, using empty map", e)
                    emptyMap()
                }
            } else {
                emptyMap()
            }

            val currentModNames = currentModulesJson.keys

            Log.d(TAG, "Current mods in JSON: $currentModNames")

            // Create updated modules map
            val updatedModules = mutableMapOf<String, Boolean>()

            // Add existing mods that still exist in directory
            currentModulesJson.forEach { (fileName, enabled) ->
                if (actualModFiles.contains(fileName)) {
                    // Keep existing state from shared preferences
                    val savedState = getModState(context, fileName, enabled)
                    updatedModules[fileName] = savedState
                } else {
                    // File was deleted, remove from preferences
                    removeModState(context, fileName)
                    Log.d(TAG, "Removed deleted mod from preferences: $fileName")
                }
            }

            // Add new mod files
            actualModFiles.forEach { fileName ->
                if (!currentModulesJson.containsKey(fileName)) {
                    // New file, use saved state or default to true
                    val savedState = getModState(context, fileName, true)
                    updatedModules[fileName] = savedState
                    saveModState(context, fileName, savedState)
                    Log.d(TAG, "Added new mod: $fileName with state: $savedState")
                }
            }

            // Write updated modules.json
            val jsonString = prettyJson.encodeToString(updatedModules)
            modulesJsonFile.writeText(jsonString)

            Log.d(TAG, "Mods refreshed successfully. Total mods: ${updatedModules.size}")
            true

        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing mods", e)
            false
        }
    }

    /**
     * Get all mods with their current states
     */
    fun getAllMods(context: Context): Map<String, Boolean>? {
        return try {
            val mediaDir = getMediaDirectory(context)
            if (mediaDir == null) {
                Log.e(TAG, "Media directory is not available")
                return null
            }

            val modulesJsonFile = File(mediaDir, MODULES_JSON)
            if (modulesJsonFile.exists()) {
                prettyJson.decodeFromString<Map<String, Boolean>>(modulesJsonFile.readText())
            } else {
                emptyMap()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all mods", e)
            null
        }
    }

    // Helper function to check if files exist in media directory
    fun checkFilesExist(context: Context): Map<String, Boolean> {
        val results = mutableMapOf<String, Boolean>()

        val mediaDir = getMediaDirectory(context)
        if (mediaDir == null) {
            Log.e(TAG, "Media directory is not available for file check")
            return results
        }

        val modulesDir = File(mediaDir, MODULES_DIR)
        val hxoIniFile = File(mediaDir, HXO_INI)
        val modulesJsonFile = File(mediaDir, MODULES_JSON)

        results["media_dir"] = mediaDir.exists() && mediaDir.isDirectory
        results["modules_dir"] = modulesDir.exists() && modulesDir.isDirectory
        results["hxo_ini"] = hxoIniFile.exists() && hxoIniFile.isFile
        results["modules_json"] = modulesJsonFile.exists() && modulesJsonFile.isFile

        Log.d(TAG, "File existence check in media directory: $results")
        Log.d(TAG, "Media directory path: ${mediaDir.absolutePath}")
        return results
    }

    // Helper function to get the media directory path (for debugging)
    fun getMediaDirectoryPath(context: Context): String? {
        return getMediaDirectory(context)?.absolutePath
    }
}