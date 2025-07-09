package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.components.PickerPopup
import com.gravatar.app.homeUi.presentation.home.components.PickerPopupItem
import com.gravatar.app.homeUi.presentation.home.components.PickerPopupMenu
import com.gravatar.ui.GravatarTheme

@Composable
internal fun AvatarMoreOptionsPickerPopup(
    anchorAlignment: Alignment.Horizontal,
    offset: DpOffset,
    onDismissRequest: () -> Unit,
    onAvatarOptionClicked: (AvatarOption) -> Unit,
) {
    PickerPopup(
        anchorAlignment = anchorAlignment,
        offset = offset,
        onDismissRequest = onDismissRequest,
        popupMenu = PickerPopupMenu(
            items = listOf(
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_tab_more_options_make_current),
                    iconRes = R.drawable.check_circle,
                    contentDescription = stringResource(
                        R.string.gravatar_tab_more_options_make_current_content_description
                    ),
                    onClick = {
                        onAvatarOptionClicked(AvatarOption.Select)
                    },
                ),
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_tab_more_options_download_avatar),
                    iconRes = R.drawable.gravatar_more_options_download,
                    contentDescription = stringResource(
                        R.string.gravatar_tab_more_options_download_avatar_content_description
                    ),
                    onClick = {
                        onAvatarOptionClicked(AvatarOption.Download)
                    },
                ),
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_tab_more_options_delete_avatar),
                    iconRes = R.drawable.delete_icon,
                    contentDescription = stringResource(
                        R.string.gravatar_tab_more_options_delete_avatar_content_description
                    ),
                    contentColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        onAvatarOptionClicked(AvatarOption.Delete)
                    },
                ),
            ),
        ),
    )
}

internal sealed class AvatarOption {
    data object Select : AvatarOption()
    data object Delete : AvatarOption()
    data object Download : AvatarOption()
}

@Preview
@Composable
private fun AvatarMoreOptionsPickerPopupPreview() {
    GravatarTheme {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(MaterialTheme.colorScheme.background),
        ) {
            AvatarMoreOptionsPickerPopup(
                anchorAlignment = Alignment.Start,
                offset = DpOffset.Zero,
                onDismissRequest = {},
                onAvatarOptionClicked = {},
            )
        }
    }
}
