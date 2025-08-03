package io.kitsuri.mayape.manager


import io.kitsuri.mayape.utils.LauncherConstants
import io.kitsuri.mayape.utils.LauncherException
import android.content.Context
import io.kitsuri.mayape.utils.FileUtils
import io.kitsuri.mayape.utils.McInfo
import java.io.File
import java.util.zip.ZipFile

/**
 * Handles DEX file operations
 */
class DexHandler(private val context: Context) {

    private val cacheDexDir = File(context.codeCacheDir, "dex")
    private lateinit var addDexPath: java.lang.reflect.Method
    private lateinit var pathList: Any

    fun setupDexFiles(mcInfo: McInfo) {
        val baseApk = findBaseApk(mcInfo.apks)
            ?: throw LauncherException("No APK contains classes.dex")

        cacheDexDir.deleteRecursively()
        cacheDexDir.mkdirs()

        setupClassLoader()
        copyLauncherDex()
        extractApkDexFiles(baseApk)
    }

    private fun findBaseApk(apks: List<String>): String? {
        return apks.firstOrNull { apk ->
            ZipFile(apk).use { zip ->
                zip.entries().asSequence().any { it.name == "classes.dex" }
            }
        }
    }

    private fun setupClassLoader() {
        val pathListField = context.classLoader.javaClass.superclass
            ?.getDeclaredField("pathList")
            ?.apply { isAccessible = true }
            ?: throw LauncherException("Unable to access pathList field")

        pathList = pathListField[context.classLoader]
        addDexPath = pathList.javaClass.getDeclaredMethod(
            "addDexPath",
            String::class.java,
            File::class.java
        )
    }

    private fun copyLauncherDex() {
        FileUtils.copyAssetToFile(
            context.assets,
            LauncherConstants.LAUNCHER,
            File(cacheDexDir, LauncherConstants.LAUNCHER)
        ).apply {
            if (!setReadOnly()) {
                throw LauncherException("Failed to set read-only for $absolutePath")
            }
            addDexPath.invoke(pathList, absolutePath, null)
        }
    }

    private fun extractApkDexFiles(baseApk: String) {
        ZipFile(baseApk).use { zipFile ->
            zipFile.entries().asSequence()
                .filter { it.name.endsWith(".dex") && !it.name.contains("/") }
                .forEach { entry ->
                    FileUtils.copyZipEntryToFile(
                        zipFile,
                        entry,
                        File(cacheDexDir, entry.name)
                    ).apply {
                        if (!setReadOnly()) {
                            throw LauncherException("Failed to set read-only for $absolutePath")
                        }
                        addDexPath.invoke(pathList, absolutePath, null)
                    }
                }
        }
    }
}