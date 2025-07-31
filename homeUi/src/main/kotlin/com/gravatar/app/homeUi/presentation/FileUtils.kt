package com.gravatar.app.homeUi.presentation

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import java.io.File

internal class FileUtils(
    private val context: Context,
) {
    fun createVCardFile(fileNameWithoutExtension: String, content: String): File {
        val directory = File(context.cacheDir, "vcard")
        directory.mkdirs()
        return File(
            directory,
            "${fileNameWithoutExtension.ifEmpty { "vcard_${System.currentTimeMillis()}" }}.vcf"
        ).apply {
            writeText(content)
        }
    }

    fun createCroppedAvatarFile(): File {
        return File(context.cacheDir, "cropped_avatar_${System.currentTimeMillis()}.jpg")
    }

    fun deleteFile(uri: Uri) {
        val toFile = uri.toFile()
        toFile.delete()
    }
}
