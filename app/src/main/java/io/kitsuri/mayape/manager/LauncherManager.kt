package io.kitsuri.mayape.manager


import android.content.Context
import android.content.pm.PackageManager

import io.kitsuri.mayape.utils.LauncherException
import io.kitsuri.mayape.utils.McInfo
import java.util.concurrent.Executors

/**
 * Manages the preparation and loading of Minecraft launcher resources
 */
class LauncherManager(private val context: Context) {

    private val executor = Executors.newSingleThreadExecutor()

    /**
     * Prepares the launcher by extracting necessary files and configuring class loader
     * @param onFinish Callback with list of APK paths when preparation is complete
     */
    fun prepareLauncher(onFinish: (List<String>) -> Unit = {}) {
        executor.execute {
            try {
                val mcInfo = getMinecraftInfo()
                val dexHandler = DexHandler(context)
                val nativeHandler = NativeLibraryHandler(context)

                // Handle native libraries
                nativeHandler.setupNativeLibraries(mcInfo)

                // Handle DEX files
                dexHandler.setupDexFiles(mcInfo)

                onFinish(mcInfo.apks)
            } catch (e: Exception) {
                throw LauncherException("Failed to prepare launcher: ${e.message}", e)
            }
        }
    }

    private fun getMinecraftInfo(): McInfo {
        val appInfo = context.packageManager.getApplicationInfo(
            "com.mojang.minecraftpe",
            PackageManager.GET_META_DATA
        )
        return McInfo(
            apks = mutableListOf(appInfo.sourceDir).apply {
                appInfo.splitSourceDirs?.let { addAll(it) }
            },
            nativeLibraryDir = appInfo.nativeLibraryDir
        )
    }
}