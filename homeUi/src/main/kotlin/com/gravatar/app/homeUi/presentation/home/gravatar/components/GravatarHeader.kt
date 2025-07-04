package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.components.AsyncImageWithCachePlaceholder
import com.gravatar.app.homeUi.presentation.home.components.GravatarAvatarWithShadow
import com.gravatar.restapi.models.Avatar
import java.net.URI

@Composable
fun GravatarHeader(
    avatar: Avatar?,
    modifier: Modifier = Modifier,
) {
    val avatarUrl = avatar?.imageUrl?.toString() ?: ""

    Box(modifier.fillMaxWidth()) {
        AsyncImageWithCachePlaceholder(
            avatarUrl,
            modifier = Modifier
                .matchParentSize()
                .blur(radius = 40.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle)
                .alpha(0.7f)
        )
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .systemBarsPadding()
        ) {
            GravatarAvatarWithShadow(
                url = avatarUrl,
                borderShape = CircleShape,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(44.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            GravatarAvatarWithShadow(
                url = avatarUrl,
                borderShape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(30.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(44.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.more_button),
                    contentDescription = stringResource(R.string.gravatar_tab_header_more_options),
                )
            }
        }
    }
}

@Preview
@Composable
fun GravatarHeaderPreview() {
    GravatarHeader(
        Avatar {
            imageUrl = URI.create("https://gravatar.com/avatar/test")
            imageId = "ID"
            rating = Avatar.Rating.G
            altText = "alt"
            updatedDate = ""
        }
    )
}
