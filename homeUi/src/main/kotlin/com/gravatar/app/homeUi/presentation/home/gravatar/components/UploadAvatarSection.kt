package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.R

@Composable
fun UploadNewAvatarSection(
    onTakePictureClicked: () -> Unit,
    onChooseFromGalleryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.gravatar_tab_get_a_new_look),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.gravatar_tab_upload_avatar_message),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NewAvatarButton(
                label = stringResource(R.string.gravatar_tab_camera),
                iconRes = R.drawable.ic_camera,
                contentDescription = stringResource(R.string.gravatar_tab_camera_content_description),
                onClick = onTakePictureClicked,
                modifier = Modifier.weight(1f)
            )
            NewAvatarButton(
                label = stringResource(R.string.gravatar_tab_photos),
                iconRes = R.drawable.ic_photo_library,
                contentDescription = stringResource(R.string.gravatar_tab_photos_content_description),
                onClick = onChooseFromGalleryClicked,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun UploadNewAvatarSectionPreview() {
    GravatarAppTheme {
        UploadNewAvatarSection(
            onTakePictureClicked = {},
            onChooseFromGalleryClicked = {}
        )
    }
}
