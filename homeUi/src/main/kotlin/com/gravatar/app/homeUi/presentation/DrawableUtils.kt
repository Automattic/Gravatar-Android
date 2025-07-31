package com.gravatar.app.homeUi.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal class DrawableUtils(private val context: Context) {

    private val imageLoader = ImageLoader(context)

    suspend fun downloadDrawable(url: String): Drawable? {
        return try {
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()
            imageLoader.execute(request).drawable
        } catch (_: Exception) {
            null
        }
    }
}

/**
 * Converts a Drawable to a Base64 encoded string.
 *
 * @param drawable The Drawable to convert.
 * @param format The desired image format (Bitmap.CompressFormat.PNG or Bitmap.CompressFormat.JPEG).
 * @param quality The quality for JPEG compression (0-100).
 * @return The Base64 encoded string representation of the image, or null if conversion fails.
 */
@OptIn(ExperimentalEncodingApi::class)
@Suppress("TooGenericExceptionCaught")
internal fun drawableToBase64(
    drawable: Drawable,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 30
): Result<String> {
    val bitmap = drawable.toBitmap()

    return try {
        val base64String = ByteArrayOutputStream().use { outputStream ->
            bitmap.compress(format, quality, outputStream)
            Base64.encode(outputStream.toByteArray())
        }
        Result.success(base64String)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
