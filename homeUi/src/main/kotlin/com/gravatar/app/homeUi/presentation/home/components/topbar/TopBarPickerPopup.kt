package com.gravatar.app.homeUi.presentation.home.components.topbar

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.components.PickerPopup
import com.gravatar.app.homeUi.presentation.home.components.PickerPopupItem
import com.gravatar.app.homeUi.presentation.home.components.PickerPopupMenu
import com.gravatar.ui.GravatarTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun TopBarPickerPopup(
    anchorAlignment: Alignment.Horizontal,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    onDismissRequest: () -> Unit,
    viewModel: TopBarPickerPopupViewModel = koinViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    action.handle(
                        context = context,
                        onDismissRequest = onDismissRequest
                    )
                }
            }
        }
    }

    TopBarPickerPopup(
        anchorAlignment = anchorAlignment,
        offset = offset,
        onDismissRequest = onDismissRequest,
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun TopBarPickerPopup(
    anchorAlignment: Alignment.Horizontal,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    onDismissRequest: () -> Unit,
    onEvent: (TopBarPickerPopupEvent) -> Unit,
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
                        onEvent(TopBarPickerPopupEvent.OnProfileLinkClicked)
                    },
                ),
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_tab_topbar_menu_share),
                    iconRes = R.drawable.top_bar_menu_share,
                    contentDescription = stringResource(
                        R.string.gravatar_tab_topbar_menu_share
                    ),
                    onClick = {
                        onEvent(TopBarPickerPopupEvent.OnShareProfileClicked)
                    },
                ),
                PickerPopupItem(
                    text = stringResource(R.string.gravatar_tab_topbar_menu_gravatar),
                    iconRes = R.drawable.top_bar_menu_gravatar,
                    contentDescription = stringResource(
                        R.string.gravatar_tab_topbar_menu_gravatar
                    ),
                    onClick = {
                        onEvent(TopBarPickerPopupEvent.OnGravatarLinkClicked)
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
                        onEvent(TopBarPickerPopupEvent.OnLogoutSelected)
                        onDismissRequest()
                    },
                ),
            )
        )
    )
}

@Suppress("LongMethod")
private fun TopBarPickerPopupAction.handle(
    context: Context,
    onDismissRequest: () -> Unit
) {
    when (this) {
        is TopBarPickerPopupAction.OpenExternalUrl -> {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)

            onDismissRequest()
        }

        is TopBarPickerPopupAction.ShareProfileUrl -> {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, url)
                type = "text/plain"
            }
            val chooserIntent = Intent.createChooser(shareIntent, null)
            context.startActivity(chooserIntent)

            onDismissRequest()
        }
    }
}

@Preview
@Composable
private fun TopBarPickerPopupPreview() {
    GravatarTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            TopBarPickerPopup(
                anchorAlignment = Alignment.End,
                onDismissRequest = {},
            )
        }
    }
}
