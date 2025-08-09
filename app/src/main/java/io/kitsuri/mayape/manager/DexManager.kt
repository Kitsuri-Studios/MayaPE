package io.kitsuri.mayape.manager

import android.content.Context
import io.kitsuri.mayape.models.TerminalViewModel
import io.kitsuri.mayape.utils.FileUtils
import io.kitsuri.mayape.utils.LauncherConstants
import io.kitsuri.mayape.utils.LauncherException
import io.kitsuri.mayape.utils.McInfo
import java.io.File
import java.util.zip.ZipFile

/**
 * Handles DEX file operations with extreme logging
 */
class DexHandler(
    private val context: Context,
    private val logger: TerminalViewModel
) {

    private val cacheDexDir = File(context.codeCacheDir, "dex")
    private lateinit var addDexPath: java.lang.reflect.Method
    private lateinit var pathList: Any

    fun setupDexFiles(mcInfo: McInfo) {
        logger.addLog("DexHandler", "CHECKPOINT", "setupDexFiles() called")
        logger.addLog("DexHandler", "DEBUG", "mcInfo.apks size = ${mcInfo.apks.size}")
        logger.addLog("DexHandler", "DEBUG", "mcInfo.apks list = ${mcInfo.apks}")

        val baseApk = findBaseApk(mcInfo.apks)
        if (baseApk == null) {
            logger.addLog("DexHandler", "ERROR", "No APK contains classes.dex")
            throw LauncherException("No APK contains classes.dex")
        }
        logger.addLog("DexHandler", "INFO", "Base APK found: $baseApk")

        logger.addLog("DexHandler", "CHECKPOINT", "Clearing and recreating cacheDexDir: ${cacheDexDir.absolutePath}")
        cacheDexDir.deleteRecursively()
        val mkdirsSuccess = cacheDexDir.mkdirs()
        logger.addLog("DexHandler", "DEBUG", "cacheDexDir.mkdirs() success = $mkdirsSuccess")

        setupClassLoader()
        copyLauncherDex()
        extractApkDexFiles(baseApk)

        logger.addLog("DexHandler", "CHECKPOINT", "setupDexFiles() completed successfully")
    }

    private fun findBaseApk(apks: List<String>): String? {
        logger.addLog("DexHandler", "CHECKPOINT", "findBaseApk() called with ${apks.size} APK(s)")
        apks.forEachIndexed { index, apk ->
            logger.addLog("DexHandler", "DEBUG", "Checking APK [$index]: $apk")
            ZipFile(apk).use { zip ->
                val containsDex = zip.entries().asSequence().any { it.name == "classes.dex" }
                logger.addLog("DexHandler", "DEBUG", "APK [$index] contains classes.dex = $containsDex")
                if (containsDex) {
                    logger.addLog("DexHandler", "INFO", "Base APK identified: $apk")
                    return apk
                }
            }
        }
        logger.addLog("DexHandler", "WARN", "No base APK with classes.dex found")
        return null
    }

    private fun setupClassLoader() {
        logger.addLog("DexHandler", "CHECKPOINT", "setupClassLoader() called")
        val classLoaderClass = context.classLoader.javaClass
        logger.addLog("DexHandler", "DEBUG", "classLoader class = ${classLoaderClass.name}")

        val pathListField = classLoaderClass.superclass
            ?.getDeclaredField("pathList")
            ?.apply { isAccessible = true }
            ?: run {
                logger.addLog("DexHandler", "ERROR", "Unable to access pathList field")
                throw LauncherException("Unable to access pathList field")
            }

        pathList = pathListField[context.classLoader]
        logger.addLog("DexHandler", "DEBUG", "pathList object acquired = $pathList")

        addDexPath = pathList.javaClass.getDeclaredMethod(
            "addDexPath",
            String::class.java,
            File::class.java
        )
        logger.addLog("DexHandler", "DEBUG", "addDexPath method acquired = ${addDexPath.name}")
    }

    private fun copyLauncherDex() {
        logger.addLog("DexHandler", "CHECKPOINT", "copyLauncherDex() called")
        val destFile = File(cacheDexDir, LauncherConstants.LAUNCHER)
        logger.addLog("DexHandler", "DEBUG", "Destination file path = ${destFile.absolutePath}")

        FileUtils.copyAssetToFile(context.assets, LauncherConstants.LAUNCHER, destFile).apply {
            logger.addLog("DexHandler", "DEBUG", "Launcher dex copied to ${absolutePath}")
            if (!setReadOnly()) {
                logger.addLog("DexHandler", "ERROR", "Failed to set read-only for $absolutePath")
                throw LauncherException("Failed to set read-only for $absolutePath")
            }
            logger.addLog("DexHandler", "DEBUG", "Launcher dex set to read-only successfully")
            addDexPath.invoke(pathList, absolutePath, null)
            logger.addLog("DexHandler", "INFO", "Launcher dex added to classloader successfully")
        }
    }

    private fun extractApkDexFiles(baseApk: String) {
        logger.addLog("DexHandler", "CHECKPOINT", "extractApkDexFiles() called for $baseApk")
        ZipFile(baseApk).use { zipFile ->
            val dexEntries = zipFile.entries().asSequence()
                .filter { it.name.endsWith(".dex") && !it.name.contains("/") }
                .toList()
            logger.addLog("DexHandler", "DEBUG", "Found ${dexEntries.size} dex entries: ${dexEntries.map { it.name }}")

            dexEntries.forEach { entry ->
                logger.addLog("DexHandler", "INFO", "Extracting dex: ${entry.name}")
                FileUtils.copyZipEntryToFile(zipFile, entry, File(cacheDexDir, entry.name)).apply {
                    logger.addLog("DexHandler", "DEBUG", "Dex file extracted to ${absolutePath}")
                    if (!setReadOnly()) {
                        logger.addLog("DexHandler", "ERROR", "Failed to set read-only for $absolutePath")
                        throw LauncherException("Failed to set read-only for $absolutePath")
                    }
                    logger.addLog("DexHandler", "DEBUG", "Dex file set to read-only successfully")
                    addDexPath.invoke(pathList, absolutePath, null)
                    logger.addLog("DexHandler", "INFO", "Dex file added to classloader successfully")
                }
            }
        }
        logger.addLog("DexHandler", "CHECKPOINT", "All dex files extracted and loaded successfully")
    }
}
