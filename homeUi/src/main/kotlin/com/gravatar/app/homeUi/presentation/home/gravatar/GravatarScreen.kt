package com.gravatar.app.homeUi.presentation.home.gravatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.presentation.home.gravatar.components.AvatarOption
import com.gravatar.app.homeUi.presentation.home.gravatar.components.UploadNewAvatarSection
import com.gravatar.app.homeUi.presentation.home.gravatar.components.avatarSize
import com.gravatar.app.homeUi.presentation.home.gravatar.components.avatarsGridSection
import com.gravatar.restapi.models.Avatar
import org.koin.androidx.compose.koinViewModel
import java.net.URI

@Composable
internal fun GravatarScreen(
    viewModel: GravatarViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    GravatarScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GravatarScreen(
    uiState: GravatarUiState,
    onEvent: (GravatarEvent) -> Unit = {},
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        val gridState = rememberLazyGridState()
        val contentPadding = PaddingValues(16.dp)
        val itemSpacing = 2.dp

        PullToRefreshBox(
            onRefresh = { onEvent(GravatarEvent.Refresh) },
            isRefreshing = uiState.isRefreshing,
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = avatarSize),
                state = gridState,
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                verticalArrangement = Arrangement.spacedBy(itemSpacing),
            ) {
                item(
                    span = { GridItemSpan((maxLineSpan)) },
                ) {
                    UploadNewAvatarSection(
                        onTakePictureClicked = { },
                        onChooseFromGalleryClicked = { }
                    )
                }
                if (uiState.isLoading) {
                    item(
                        span = { GridItemSpan((maxLineSpan)) },
                    ) {
                        Box(
                            modifier = Modifier
                                .height(200.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    avatarsGridSection(
                        avatars = uiState.avatarsUi,
                        onAvatarOptionClicked = { avatar, option ->
                            when (option) {
                                AvatarOption.Select -> {
                                    onEvent(GravatarEvent.OnAvatarSelected(avatar.imageId))
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GravatarScreenPreview() {
    GravatarScreen(
        uiState = GravatarUiState(
            isLoading = false,
            avatars = List(10) {
                Avatar {
                    imageUrl = URI.create("https://gravatar.com/avatar/test")
                    imageId = it.toString()
                    rating = Avatar.Rating.G
                    altText = "alt"
                    updatedDate = ""
                }
            }
        ),
    )
}
