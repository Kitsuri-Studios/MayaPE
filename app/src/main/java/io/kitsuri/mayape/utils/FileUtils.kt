package io.kitsuri.mayape.utils

import android.content.res.AssetManager
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Utility class for file operations
 */
object FileUtils {

    fun copyAssetToFile(assets: AssetManager,
                        assetPath: String,
                        targetFile: File): File {
        assets.open(assetPath).use { input ->
            targetFile.parentFile?.mkdirs()
                ?: throw LauncherException("Failed to create directories: ${targetFile.parent}")
            Files.copy(
                input,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
        return targetFile
    }

    fun copyZipEntryToFile(zipFile: ZipFile,
                           entry: ZipEntry,
                           targetFile: File): File {
        zipFile.getInputStream(entry).use { input ->
            targetFile.parentFile?.mkdirs()
                ?: throw LauncherException("Failed to create directories: ${targetFile.parent}")
            Files.copy(
                input,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
        return targetFile
    }
}