package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun ExpandedQrCode(
    qrCodeData: String,
    avatarUrl: String,
    onDismissRequest: () -> Unit,
) {
    BackHandler {
        onDismissRequest()
    }
    HideNavigationBar()
    Surface {
        BlurredHeaderBackground(
            avatarUrl = avatarUrl,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.statusBarsPadding()) {
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
                        contentDescription = stringResource(R.string.close_button),
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                QrCode(qrCodeData = qrCodeData)
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
private fun HideNavigationBar() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = context.findComponentActivity()?.window ?: return@DisposableEffect onDispose {}
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        insetsController.apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            insetsController.apply {
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
            qrCodeData = "BEGIN:VCARD\nVERSION:3.0\nFN:John Doe\nEMAIL:",
            avatarUrl = "https://gravatar.com/avatar/test",
            onDismissRequest = {}
        )
    }
}
