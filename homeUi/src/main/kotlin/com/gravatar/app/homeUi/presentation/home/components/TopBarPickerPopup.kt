package com.gravatar.app.homeUi.presentation.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun TopBarPickerPopup(
    anchorAlignment: Alignment.Horizontal,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    onDismissRequest: () -> Unit,
    onTopBarOptionClicked: (TopBarOption) -> Unit,
) {
    PickerPopup(
        anchorAlignment = anchorAlignment,
        offset = offset,
        onDismissRequest = onDismissRequest,
        popupMenu = PickerPopupMenu(
            items = listOf(
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_tab_topbar_menu_visit_profile),
                    iconRes = R.drawable.top_bar_menu_profile,
                    contentDescription = stringResource(
                        R.string.gravatar_tab_topbar_menu_visit_profile
                    ),
                    onClick = {
                        onTopBarOptionClicked(TopBarOption.Profile)
                    },
                ),
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_tab_topbar_menu_share),
                    iconRes = R.drawable.top_bar_menu_share,
                    contentDescription = stringResource(
                        R.string.gravatar_tab_topbar_menu_share
                    ),
                    onClick = {
                        onTopBarOptionClicked(TopBarOption.Share)
                    },
                ),
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_tab_topbar_menu_gravatar),
                    iconRes = R.drawable.top_bar_menu_gravatar,
                    contentDescription = stringResource(
                        R.string.gravatar_tab_topbar_menu_gravatar
                    ),
                    onClick = {
                        onTopBarOptionClicked(TopBarOption.Gravatar)
                    },
                ),
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_tab_topbar_menu_sign_out),
                    iconRes = R.drawable.top_bar_menu_logout,
                    contentDescription = stringResource(
                        R.string.gravatar_tab_topbar_menu_sign_out
                    ),
                    contentColor = MaterialTheme.colorScheme.error,
                    onClick = {
                        onTopBarOptionClicked(TopBarOption.Logout)
                    },
                ),
            )
        )
    )
}

internal sealed class TopBarOption {
    data object Logout : TopBarOption()
    data object Share : TopBarOption()
    data object Profile : TopBarOption()
    data object Gravatar : TopBarOption()
}

@Preview
@Composable
private fun TopBarPickerPopupPreview() {
    GravatarTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            TopBarPickerPopup(
                anchorAlignment = Alignment.End,
                onDismissRequest = {},
                onTopBarOptionClicked = {}
            )
        }
    }
}
