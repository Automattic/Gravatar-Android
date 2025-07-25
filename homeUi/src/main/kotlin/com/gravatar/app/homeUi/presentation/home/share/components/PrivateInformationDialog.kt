package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.design.components.button.PrimaryButton
import com.gravatar.app.design.components.dialog.DialogText
import com.gravatar.app.design.components.dialog.DialogTitle
import com.gravatar.app.design.components.dialog.GravatarDialog
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.R

@Composable
internal fun PrivateInformationDialog(
    onDismissRequest: () -> Unit,
) {
    GravatarDialog(
        onDismissRequest = onDismissRequest,
        content = {
            PrivateInformationDialogContent(
                onDismissRequest = onDismissRequest,
            )
        }
    )
}

@Composable
internal fun PrivateInformationDialogContent(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(24.dp)) {
        DialogTitle(
            title = stringResource(R.string.private_info_dialog_title),
        )
        DialogText(
            text = stringResource(R.string.private_info_dialog_message),
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
        PrimaryButton(
            text = stringResource(R.string.private_info_button_cta),
            onClick = onDismissRequest,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrivateInformationDialogContentPreview() {
    GravatarAppTheme {
        PrivateInformationDialogContent(
            onDismissRequest = {}
        )
    }
}
