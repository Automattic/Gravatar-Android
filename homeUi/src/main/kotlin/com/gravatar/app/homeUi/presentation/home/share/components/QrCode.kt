package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.design.theme.GravatarAppTheme
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@Composable
fun QrCode(
    qrCodeData: String,
    modifier: Modifier = Modifier,
) {
    val qrcodePainter: Painter = rememberQrCodePainter(qrCodeData) {
        shapes {
            ball = QrBallShape.roundCorners(.30f)
            darkPixel = QrPixelShape.roundCorners()
            frame = QrFrameShape.roundCorners(.15f)
        }
    }

    Image(
        painter = qrcodePainter,
        contentDescription = null,
        modifier = modifier
            .background(Color.White, RoundedCornerShape(4.dp))
            .fillMaxWidth()
            .aspectRatio(
                ratio = 1f,
                matchHeightConstraintsFirst = false,
            )
            .padding(8.dp)
    )
}

@Preview
@Composable
private fun QrCodePreview() {
    GravatarAppTheme {
        QrCode(qrCodeData = "BEGIN:VCARD\nVERSION:3.0\nN:Doe;John;;;\nFN:John Doe\nORG:Gravatar App\nEMAIL:")
    }
}
