package com.gravatar.app.homeUi.presentation.home.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toDrawable
import coil.compose.AsyncImage
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
internal fun AsyncImageWithCachePlaceholder(
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
