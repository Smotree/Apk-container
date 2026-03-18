package com.apkcontainer.util

import android.content.Context
import android.net.Uri
import java.io.File

object FileUtils {
    fun copyUriToCache(context: Context, uri: Uri): String? {
        return try {
            val cacheDir = File(context.cacheDir, "apks")
            cacheDir.mkdirs()
            val fileName = "apk_${System.currentTimeMillis()}.apk"
            val outputFile = File(cacheDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            outputFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "%.1f KB".format(bytes / 1024.0)
            bytes < 1024 * 1024 * 1024 -> "%.1f MB".format(bytes / (1024.0 * 1024))
            else -> "%.1f GB".format(bytes / (1024.0 * 1024 * 1024))
        }
    }
}
