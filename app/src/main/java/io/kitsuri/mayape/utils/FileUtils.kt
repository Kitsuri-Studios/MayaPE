package io.kitsuri.mayape.utils

import android.content.Context
import android.content.res.AssetManager
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
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

    fun uriToFilePath(context: Context, uri: Uri): String? {
        val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        } ?: return null

        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(context.cacheDir, fileName)
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }
        return tempFile.absolutePath
    }

}