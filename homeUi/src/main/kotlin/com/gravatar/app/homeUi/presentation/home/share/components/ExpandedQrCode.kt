package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.findComponentActivity
import com.gravatar.app.homeUi.presentation.home.components.BlurredHeaderBackground
import com.gravatar.app.homeUi.presentation.home.profile.header.MENU_BUTTON_SIZE
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.launch

@Composable
fun ExpandedQrCode(
    vCardQrCodeData: String,
    avatarUrl: String,
    onDismissRequest: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    BackHandler {
        coroutineScope.launch {
            onDismissRequest()
        }
    }
    HideSystemBars()
    Surface {
        BlurredHeaderBackground(
            avatarUrl = avatarUrl,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(verticalAlignment = Alignment.Top) {
                IconButton(
                    onClick = {
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(MENU_BUTTON_SIZE)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.close_button),
                        contentDescription = stringResource(R.string.gravatar_tab_header_more_options),
                    )
                }
            }

            val qrcodePainter: Painter = rememberQrCodePainter(vCardQrCodeData) {
                shapes {
                    ball = QrBallShape.roundCorners(.30f)
                    darkPixel = QrPixelShape.roundCorners()
                    frame = QrFrameShape.roundCorners(.15f)
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Image(
                    painter = qrcodePainter,
                    contentDescription = null,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .fillMaxWidth()
                        .aspectRatio(
                            ratio = 1f,
                            matchHeightConstraintsFirst = false,
                        )
                        .padding(16.dp)
                )
                Text(
                    text = stringResource(R.string.share_tab_scan_qr_code),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
private fun HideSystemBars() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = context.findComponentActivity()?.window ?: return@DisposableEffect onDispose {}
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        insetsController.apply {
            hide(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            insetsController.apply {
                show(WindowInsetsCompat.Type.statusBars())
                show(WindowInsetsCompat.Type.navigationBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }
}

@Preview
@Composable
fun ExpandedQrCodeDialogPreview() {
    GravatarAppTheme {
        ExpandedQrCode(
            vCardQrCodeData = "BEGIN:VCARD\nVERSION:3.0\nFN:John Doe\nEMAIL:",
            avatarUrl = "https://gravatar.com/avatar/test",
            onDismissRequest = {}
        )
    }
}
