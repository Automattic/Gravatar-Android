package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
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
                    text = "Select",
                    iconRes = null,
                    contentDescription = "Select avatar",
                    onClick = {
                        onAvatarOptionClicked(AvatarOption.Select)
                    },
                ),
                PickerPopupItem(
                    text = "Option 2",
                    iconRes = null,
                    contentDescription = "Option 2",
                    onClick = { },
                ),
            ),
        ),
    )
}

internal sealed class AvatarOption {
    data object Select : AvatarOption()
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
