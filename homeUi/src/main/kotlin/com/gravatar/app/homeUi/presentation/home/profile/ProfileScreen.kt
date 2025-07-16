package com.gravatar.app.homeUi.presentation.home.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.gravatar.app.design.components.snackbar.SnackbarType
import com.gravatar.app.design.components.snackbar.showGravatarSnackbar
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutInputField
import com.gravatar.app.homeUi.presentation.home.profile.about.AboutSection
import com.gravatar.app.homeUi.presentation.home.profile.header.AnimatedProfileHeader
import com.gravatar.app.homeUi.presentation.home.profile.header.ProfileHeaderSaveState
import com.gravatar.extensions.defaultProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
internal fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel(), snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val lifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    action.handle(
                        context = context,
                        snackbarHostState = snackbarHostState,
                        scope = scope,
                    )
                }
            }
        }
    }

    ProfileScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(profileEvent = event)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(uiState: ProfileUiState, onEvent: (ProfileEvent) -> Unit) {
    val scrollState = rememberScrollState()
    // Calculate scroll fraction (0 to 1) for animation
    val maxScrollForAnimation = 400f
    val scrollFraction = if (scrollState.isScrollInProgress) {
        (scrollState.value / maxScrollForAnimation).coerceIn(0f, 1f)
    } else {
        (scrollState.value / maxScrollForAnimation).roundToInt().coerceIn(0, 1).toFloat()
    }

    PullToRefreshBox(
        enabled = uiState.pullToRefreshEnabled,
        onRefresh = { onEvent(ProfileEvent.OnRefreshProfile) },
        isRefreshing = uiState.isRefreshing,
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
    ) {
        if (uiState.showLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            uiState.profile.let { profile ->
                if (profile != null) {
                    Column {
                        AnimatedProfileHeader(
                            profile = profile,
                            avatarUrl = uiState.avatarUrl,
                            saveState = when {
                                uiState.isSavingProfile -> ProfileHeaderSaveState.SAVING
                                uiState.hasUnsavedChanges -> ProfileHeaderSaveState.UNSAVED
                                else -> ProfileHeaderSaveState.SAVED
                            },
                            onSaveProfile = { onEvent(ProfileEvent.OnSaveClicked) },
                            scrollPosition = scrollFraction
                        )
                        Column(
                            Modifier
                                .verticalScroll(scrollState)
                                .padding(vertical = 16.dp)
                        ) {
                            AboutSection(
                                aboutFields = uiState.aboutFields,
                                formEnabled = !uiState.isSavingProfile,
                                onValueChange = {
                                    onEvent(ProfileEvent.OnProfileFieldUpdated(it))
                                },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                } else {
                    Text("There was an error retrieving the profile.")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PullToRefreshBox(
    enabled: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = rememberPullToRefreshState()
    Box(
        modifier = modifier
            .pullToRefresh(
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                enabled = enabled,
                state = state
            )
    ) {
        content()
        PullToRefreshDefaults.Indicator(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .displayCutoutPadding(),
            isRefreshing = isRefreshing,
            state = state,
        )
    }
}

private fun ProfileAction.handle(
    context: android.content.Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
) {
    when (this) {
        is ProfileAction.ProfileSaved -> {
            scope.launch {
                snackbarHostState.showGravatarSnackbar(
                    message = context.getString(R.string.profile_screen_saved_successfully),
                    withDismissAction = true,
                    snackbarType = SnackbarType.Info,
                )
            }
        }

        is ProfileAction.ProfileSaveFailed -> {
            scope.launch {
                snackbarHostState.showGravatarSnackbar(
                    message = context.getString(R.string.profile_screen_save_fail_try_again),
                    withDismissAction = true,
                    snackbarType = SnackbarType.Error,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        uiState = ProfileUiState(
            isLoading = false,
            profile = defaultProfile(
                hash = "",
                displayName = "John Doe",
                jobTitle = "Software Engineer",
                company = "Automattic"
            ),
            editedAboutFields = mapOf(
                AboutInputField.DISPLAY_NAME to "John Doe",
            ),
        ),
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun AnimatedProfileHeaderExpandedPreview() {
    AnimatedProfileHeader(
        profile = defaultProfile(
            hash = "",
            displayName = "John Doe",
            jobTitle = "Software Engineer",
            company = "Automattic"
        ),
        avatarUrl = "https://gravatar.com/avatar/test",
        saveState = ProfileHeaderSaveState.UNSAVED,
        onSaveProfile = {},
        scrollPosition = 0f // Fully expanded
    )
}

@Preview(showBackground = true)
@Composable
private fun AnimatedProfileHeaderCollapsedPreview() {
    AnimatedProfileHeader(
        profile = defaultProfile(
            hash = "",
            displayName = "John Doe",
            jobTitle = "Software Engineer",
            company = "Automattic"
        ),
        avatarUrl = "https://gravatar.com/avatar/test",
        saveState = ProfileHeaderSaveState.UNSAVED,
        onSaveProfile = {},
        scrollPosition = 1f // Fully collapsed
    )
}

@Preview(showBackground = true)
@Composable
private fun AnimatedProfileHeaderTransitionPreview() {
    AnimatedProfileHeader(
        profile = defaultProfile(
            hash = "",
            displayName = "John Doe",
            jobTitle = "Software Engineer",
            company = "Automattic"
        ),
        avatarUrl = "https://gravatar.com/avatar/test",
        saveState = ProfileHeaderSaveState.UNSAVED,
        onSaveProfile = {},
        scrollPosition = 0.5f // Mid-transition
    )
}
