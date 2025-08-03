package io.kitsuri.mayape.manager

import android.content.Context
import io.kitsuri.mayape.utils.LauncherConstants
import io.kitsuri.mayape.utils.LauncherException
import java.io.File
import java.util.zip.ZipFile
import io.kitsuri.mayape.utils.FileUtils
import io.kitsuri.mayape.utils.McInfo

/**
 * Handles native library operations
 */
class NativeLibraryHandler(private val context: Context) {

    fun setupNativeLibraries(mcInfo: McInfo) {
        val libApk = findLibraryApk(mcInfo.apks)
            ?: throw LauncherException("No arm64-v8a libraries found")

        val nativeDir = if (mcInfo.nativeLibraryDir.isNullOrEmpty() ||
            File(mcInfo.nativeLibraryDir).listFiles().isNullOrEmpty()) {
            extractNativeLibraries(libApk)
        } else {
            mcInfo.nativeLibraryDir
        }

        setupNativePath(nativeDir)
    }

    private fun findLibraryApk(apks: List<String>): String? {
        return apks.firstOrNull { apk ->
            ZipFile(apk).use { zip ->
                zip.entries().asSequence().any {
                    it.name.startsWith(LauncherConstants.LIBS)
                }
            }
        }
    }

    private fun extractNativeLibraries(libApk: String): String {
        val outputDir = File(context.codeCacheDir, LauncherConstants.LIBS)
        ZipFile(libApk).use { zipFile ->
            zipFile.entries().asSequence()
                .filter { !it.isDirectory && it.name.startsWith(LauncherConstants.LIBS) }
                .forEach { entry ->
                    FileUtils.copyZipEntryToFile(
                        zipFile,
                        entry,
                        File(context.codeCacheDir, entry.name)
                    )
                }
        }
        return outputDir.absolutePath
    }

    private fun setupNativePath(nativeDir: String) {
        val pathListField = context.classLoader.javaClass.superclass
            ?.getDeclaredField("pathList")
            ?.apply { isAccessible = true }
            ?: throw LauncherException("Unable to access pathList field")

        val pathList = pathListField[context.classLoader]
        val addNativePath = pathList.javaClass.getDeclaredMethod(
            "addNativePath",
            MutableCollection::class.java
        )
        addNativePath.invoke(pathList, listOf(nativeDir))
    }
}