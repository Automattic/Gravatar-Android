package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gravatar.restapi.models.Avatar
import com.gravatar.ui.components.ComponentState
import java.net.URI
import com.gravatar.ui.components.atomic.Avatar as GravatarAvatar

@Composable
fun GravatarHeader(
    avatar: Avatar?,
    modifier: Modifier = Modifier,
) {
    val avatarUrl = avatar?.imageUrl?.toString()
    avatarUrl?.let { url ->
        Box(modifier.fillMaxWidth()) {
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .blur(
                        radius = 24.dp,
                        edgeTreatment = BlurredEdgeTreatment.Rectangle,
                    )
            )
            Row(
                Modifier.padding(16.dp)
            ) {
                GravatarAvatar(
                    state = ComponentState.Loaded(url),
                    size = 44.dp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(8.dp))
                GravatarAvatar(
                    state = ComponentState.Loaded(url),
                    size = 30.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .align(Alignment.CenterVertically)
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
