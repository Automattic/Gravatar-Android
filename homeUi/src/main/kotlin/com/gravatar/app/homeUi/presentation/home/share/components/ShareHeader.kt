package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.components.BlurredHeaderBackground
import com.gravatar.app.homeUi.presentation.home.components.topbar.TopBarPickerPopup
import com.gravatar.app.homeUi.presentation.home.profile.header.MENU_BUTTON_SIZE

@Composable
internal fun ShareHeader(
    avatarUrl: String,
    modifier: Modifier = Modifier,
    onAboutAppClicked: () -> Unit = {},
) {
    var topBarMenuVisible by remember { mutableStateOf(false) }

    BlurredHeaderBackground(
        avatarUrl = avatarUrl,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(top = 6.dp)
                    .weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .fillMaxWidth()
                        .aspectRatio(
                            ratio = 1f,
                            matchHeightConstraintsFirst = false,
                        )
                )
                Text(
                    text = stringResource(R.string.share_tab_scan_qr_code),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                )
            }
            Column {
                Box {
                    IconButton(
                        onClick = {
                            topBarMenuVisible = true
                        },
                        modifier = Modifier
                            .size(MENU_BUTTON_SIZE)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.more_button),
                            contentDescription = stringResource(R.string.gravatar_tab_header_more_options),
                        )
                    }
                    if (topBarMenuVisible) {
                        TopBarPickerPopup(
                            anchorAlignment = Alignment.End,
                            offset = DpOffset(0.dp, 6.dp),
                            onDismissRequest = { topBarMenuVisible = false },
                            onAboutAppClicked = {
                                topBarMenuVisible = false
                                onAboutAppClicked()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ShareHeaderPreview() {
    GravatarAppTheme {
        ShareHeader(
            avatarUrl = "url",
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
