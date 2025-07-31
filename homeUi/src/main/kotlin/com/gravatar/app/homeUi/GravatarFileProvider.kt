package com.gravatar.app.homeUi

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

internal class GravatarFileProvider : FileProvider(R.xml.gravatar_filepaths) {
    companion object Companion {
        fun getTempCameraImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File(directory, "temp_camera_image.jpg")
            val authority = "${context.packageName}.fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }

        fun getFileUri(context: Context, file: File): Uri {
            val authority = "${context.packageName}.fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}
