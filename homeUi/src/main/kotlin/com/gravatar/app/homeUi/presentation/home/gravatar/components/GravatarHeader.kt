package com.gravatar.app.homeUi.presentation.home.gravatar.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import coil.compose.AsyncImage
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.gravatar.app.homeUi.R
import com.gravatar.restapi.models.Avatar
import java.net.URI

@Composable
fun GravatarHeader(
    avatar: Avatar?,
    modifier: Modifier = Modifier,
) {
    val avatarUrl = avatar?.imageUrl?.toString() ?: ""

    Box(modifier.fillMaxWidth()) {
        GravatarAvatar(
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
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.more_button),
                    contentDescription = stringResource(R.string.gravatar_tab_header_more_options)
                )
            }
        }
    }
}

@Composable
private fun GravatarAvatarWithShadow(
    url: String,
    borderShape: RoundedCornerShape,
    modifier: Modifier = Modifier,
) {
    val colorStops = remember {
        arrayOf(
            0.0f to Color.Black.copy(0.3f),
            0.6f to Color.Black.copy(alpha = 0f)
        )
    }

    var loaded by remember {
        mutableStateOf(false)
    }

    val finalModifier = remember(loaded, borderShape) {
        if (loaded) {
            modifier
                .clip(borderShape)
                .background(Brush.linearGradient(colorStops = colorStops))
                .padding(1.dp)
                .shadow(1.dp, borderShape)
        } else {
            modifier
                .padding(1.dp)
                .clip(borderShape)
        }
    }

    Box(
        modifier = finalModifier
    ) {
        GravatarAvatar(url, modifier.clip(borderShape), onLoadedState = {
            loaded = it
        })
    }
}

@Composable
private fun GravatarAvatar(
    url: String,
    modifier: Modifier = Modifier,
    onLoadedState: (Boolean) -> Unit = {},
) {
    var oldImage: MemoryCache.Key? by remember {
        mutableStateOf(null)
    }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .placeholderMemoryCacheKey(oldImage)
            .placeholder(Color.LightGray.toArgb().toDrawable())
            .listener { _, successResult ->
                oldImage = successResult.memoryCacheKey
            }
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        onLoading = {
            onLoadedState.invoke(false)
        },
        onSuccess = {
            onLoadedState.invoke(true)
        },
        modifier = modifier
    )
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
