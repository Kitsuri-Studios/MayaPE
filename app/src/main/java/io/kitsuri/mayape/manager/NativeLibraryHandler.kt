package io.kitsuri.mayape.manager

import android.content.Context
import io.kitsuri.mayape.models.TerminalViewModel
import io.kitsuri.mayape.utils.LauncherConstants
import io.kitsuri.mayape.utils.LauncherException
import java.io.File
import java.util.zip.ZipFile
import io.kitsuri.mayape.utils.FileUtils
import io.kitsuri.mayape.utils.McInfo
class NativeLibraryHandler(
    private val context: Context,
    private val logger: TerminalViewModel
) {

    fun setupNativeLibraries(mcInfo: McInfo) {
        logger.addLog("NativeLibHandler", "START", "setupNativeLibraries() called with mcInfo: $mcInfo")

        val libApk = findLibraryApk(mcInfo.apks)
            ?: run {
                logger.addLog("NativeLibHandler", "ERROR", "No arm64-v8a libraries found")
                throw LauncherException("No arm64-v8a libraries found")
            }
        logger.addLog("NativeLibHandler", "INFO", "Library APK found: $libApk")

        val nativeDir = if (mcInfo.nativeLibraryDir.isNullOrEmpty() ||
            File(mcInfo.nativeLibraryDir).listFiles().isNullOrEmpty()) {
            logger.addLog("NativeLibHandler", "CHECK", "Native library dir missing/empty, extracting from APK")
            extractNativeLibraries(libApk)
        } else {
            logger.addLog("NativeLibHandler", "INFO", "Using existing native library dir: ${mcInfo.nativeLibraryDir}")
            mcInfo.nativeLibraryDir
        }

        setupNativePath(nativeDir)
        logger.addLog("NativeLibHandler", "END", "setupNativeLibraries() completed successfully")
    }

    private fun findLibraryApk(apks: List<String>): String? {
        logger.addLog("NativeLibHandler", "START", "findLibraryApk() scanning ${apks.size} APK(s)")
        val result = apks.firstOrNull { apk ->
            ZipFile(apk).use { zip ->
                val found = zip.entries().asSequence().any {
                    it.name.startsWith(LauncherConstants.LIBS)
                }
                logger.addLog("NativeLibHandler", "CHECK", "APK: $apk | has native libs = $found")
                found
            }
        }
        logger.addLog("NativeLibHandler", "END", "findLibraryApk() result: $result")
        return result
    }

    private fun extractNativeLibraries(libApk: String): String {
        logger.addLog("NativeLibHandler", "START", "extractNativeLibraries() from APK: $libApk")
        val outputDir = File(context.codeCacheDir, LauncherConstants.LIBS)
        logger.addLog("NativeLibHandler", "INFO", "Output dir: ${outputDir.absolutePath}")

        ZipFile(libApk).use { zipFile ->
            zipFile.entries().asSequence()
                .filter { !it.isDirectory && it.name.startsWith(LauncherConstants.LIBS) }
                .forEach { entry ->
                    logger.addLog("NativeLibHandler", "INFO", "Extracting library entry: ${entry.name}")
                    FileUtils.copyZipEntryToFile(
                        zipFile,
                        entry,
                        File(context.codeCacheDir, entry.name)
                    )
                }
        }
        logger.addLog("NativeLibHandler", "END", "Native libraries extracted to: ${outputDir.absolutePath}")
        return outputDir.absolutePath
    }

    private fun setupNativePath(nativeDir: String) {
        logger.addLog("NativeLibHandler", "START", "setupNativePath() with nativeDir: $nativeDir")
        val pathListField = context.classLoader.javaClass.superclass
            ?.getDeclaredField("pathList")
            ?.apply { isAccessible = true }
            ?: run {
                logger.addLog("NativeLibHandler", "ERROR", "Unable to access pathList field")
                throw LauncherException("Unable to access pathList field")
            }

        val pathList = pathListField[context.classLoader]
        val addNativePath = pathList.javaClass.getDeclaredMethod(
            "addNativePath",
            MutableCollection::class.java
        )

        addNativePath.invoke(pathList, listOf(nativeDir))
        logger.addLog("NativeLibHandler", "INFO", "Native path added to class loader: $nativeDir")
        logger.addLog("NativeLibHandler", "END", "setupNativePath() completed")
    }
}
