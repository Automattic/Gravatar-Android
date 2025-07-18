package com.gravatar.app.homeUi.presentation.home.gravatar.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.gravatar.AvatarUi
import com.gravatar.restapi.models.Avatar
import java.net.URI

internal fun LazyGridScope.avatarsGridSection(
    avatars: List<AvatarUi>,
    onAvatarOptionClicked: (Avatar, AvatarOption) -> Unit,
    onFailedAvatarClicked: ((Uri) -> Unit)? = null,
) {
    item(
        span = { GridItemSpan((maxLineSpan)) },
    ) {
        AvatarsGridHeader(
            isGridEmpty = avatars.isEmpty(),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
    if (avatars.isEmpty()) {
        item(
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
        }
    } else {
        if (avatars.none { it is AvatarUi.Uploaded && it.isSelected }) {
            item(
                span = { GridItemSpan(maxLineSpan) },
            ) {
                NoAvatarSelectedBanner(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
            }
        }
        item(
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Spacer(Modifier.height(10.dp))
        }
        items(items = avatars, key = { it.avatarId }) { avatarModel ->
            SelectableAvatar(
                avatar = avatarModel,
                size = avatarSize,
                modifier = Modifier
                    .animateItem(),
                onAvatarOptionClicked = onAvatarOptionClicked,
                onFailedAvatarClicked = onFailedAvatarClicked,
            )
        }
    }
}

@Composable
private fun AvatarsGridHeader(
    isGridEmpty: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.gravatar_tab_previous_avatars),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(
                if (isGridEmpty) {
                    R.string.gravatar_tab_empty_avatars
                } else {
                    R.string.gravatar_tab_tap_for_options
                }
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun NoAvatarSelectedBanner(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.gravatar_tab_no_avatar_selected_info),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.inverseOnSurface,
        textAlign = TextAlign.Center,
        modifier = modifier
            .background(MaterialTheme.colorScheme.inverseSurface, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    )
}

@Composable
@Preview(showBackground = true)
private fun AvatarsGridSectionPreview() {
    MaterialTheme {
        Box {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = avatarSize),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = rememberLazyGridState(),
            ) {
                avatarsGridSection(
                    avatars = List(6) {
                        AvatarUi.Uploaded(
                            avatar = Avatar {
                                imageUrl = URI.create("https://gravatar.com/avatar/test")
                                imageId = it.toString()
                                rating = Avatar.Rating.G
                                altText = "alt"
                                updatedDate = ""
                            },
                            isSelected = it == 0,
                            isLoading = false,
                        )
                    },
                    onAvatarOptionClicked = { _, _ -> },
                    onFailedAvatarClicked = { },
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AvatarsGridSectionNoAvatarSelectedPreview() {
    MaterialTheme {
        Box {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = avatarSize),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = rememberLazyGridState(),
            ) {
                avatarsGridSection(
                    avatars = List(6) {
                        AvatarUi.Uploaded(
                            avatar = Avatar {
                                imageUrl = URI.create("https://gravatar.com/avatar/test")
                                imageId = it.toString()
                                rating = Avatar.Rating.G
                                altText = "alt"
                                updatedDate = ""
                            },
                            isSelected = false,
                            isLoading = false,
                        )
                    },
                    onAvatarOptionClicked = { _, _ -> },
                    onFailedAvatarClicked = { },
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AvatarsGridSectionEmptyPreview() {
    MaterialTheme {
        Box {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = avatarSize),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = rememberLazyGridState(),
            ) {
                avatarsGridSection(
                    avatars = emptyList(),
                    onAvatarOptionClicked = { _, _ -> },
                    onFailedAvatarClicked = { },
                )
            }
        }
    }
}
