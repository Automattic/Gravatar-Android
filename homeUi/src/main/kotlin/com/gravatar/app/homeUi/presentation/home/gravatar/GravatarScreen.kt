package com.gravatar.app.homeUi.presentation.home.gravatar

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.gravatar.app.design.components.snackbar.SnackbarType
import com.gravatar.app.design.components.snackbar.showGravatarSnackbar
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.GravatarFileProvider
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.components.ErrorViewWithRetry
import com.gravatar.app.homeUi.presentation.home.components.PermissionRationaleDialog
import com.gravatar.app.homeUi.presentation.home.components.topbar.components.AboutAppDialog
import com.gravatar.app.homeUi.presentation.home.gravatar.components.AvatarDeletionConfirmationDialog
import com.gravatar.app.homeUi.presentation.home.gravatar.components.AvatarOption
import com.gravatar.app.homeUi.presentation.home.gravatar.components.CollapsibleTopAppBar
import com.gravatar.app.homeUi.presentation.home.gravatar.components.FailedAvatarUploadAlertDialog
import com.gravatar.app.homeUi.presentation.home.gravatar.components.GRAVATAR_HEADER_COLLAPSED_HEIGHT
import com.gravatar.app.homeUi.presentation.home.gravatar.components.GravatarHeader
import com.gravatar.app.homeUi.presentation.home.gravatar.components.UploadNewAvatarSection
import com.gravatar.app.homeUi.presentation.home.gravatar.components.avatarSize
import com.gravatar.app.homeUi.presentation.home.gravatar.components.avatarsGridSection
import com.gravatar.app.homeUi.presentation.home.gravatar.components.rememberExpansionProgress
import com.gravatar.app.homeUi.presentation.home.profile.PullToRefreshBox
import com.gravatar.app.homeUi.presentation.openAppPermissionSettings
import com.gravatar.app.homeUi.presentation.withPermission
import com.gravatar.restapi.models.Avatar
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.net.URI

@Composable
internal fun GravatarScreen(
    viewModelStoreOwner: ViewModelStoreOwner,
    viewModel: GravatarViewModel = koinViewModel(viewModelStoreOwner = viewModelStoreOwner),
    snackbarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val lifecycle = LocalLifecycleOwner.current
    val context = LocalContext.current
    var photoImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var mediaPickerLaunched by rememberSaveable { mutableStateOf(false) }

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        mediaPickerLaunched = false
        uri?.let { viewModel.onEvent(GravatarEvent.OnLocalImageSelected(uri)) }
    }

    val uCropLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        it.data?.let { intentData ->
            UCrop.getOutput(intentData)?.let { croppedImageUri ->
                viewModel.onEvent(GravatarEvent.OnImageCropped(croppedImageUri))
            }
        }
    }

    val takePhoto = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val takenPictureUri = photoImageUri
        if (success && takenPictureUri != null) {
            viewModel.onEvent(GravatarEvent.OnLocalImageSelected(takenPictureUri))
        }
    }

    val takePhotoCallback = {
        val imageUri = GravatarFileProvider.getTempCameraImageUri(context)
        photoImageUri = imageUri
        takePhoto.launch(imageUri)
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect { action ->
                    action.handle(
                        context = context,
                        uCropLauncher = uCropLauncher,
                        snackbarHostState = snackbarHostState,
                        scope = scope,
                    )
                }
            }
        }
    }

    GravatarScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onTakePictureClicked = takePhotoCallback,
        onPickMediaClicked = {
            if (!mediaPickerLaunched) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                mediaPickerLaunched = true
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GravatarScreen(
    uiState: GravatarUiState,
    onTakePictureClicked: () -> Unit,
    onPickMediaClicked: () -> Unit,
    onEvent: (GravatarEvent) -> Unit = {},
) {
    val context = LocalContext.current
    var storagePermissionRationaleDialogVisible by rememberSaveable { mutableStateOf(false) }
    var avatarToDownload: Avatar? by remember { mutableStateOf(null) }

    val writeExternalStoragePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            avatarToDownload?.let { onEvent(GravatarEvent.OnDownloadAvatar(it.imageId)) }
        } else {
            storagePermissionRationaleDialogVisible = true
        }
        avatarToDownload = null
    }

    val permissionAwareDownloadImageCallback: (Avatar) -> Unit = { avatar ->
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            context.withPermission(
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
                onRequestPermission = {
                    avatarToDownload = avatar
                    writeExternalStoragePermissionLauncher.launch(it)
                },
                onShowRationale = { storagePermissionRationaleDialogVisible = true },
                grantedCallback = {
                    onEvent(GravatarEvent.OnDownloadAvatar(avatar.imageId))
                },
            )
        } else {
            onEvent(GravatarEvent.OnDownloadAvatar(avatar.imageId))
        }
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val expansionProgress = rememberExpansionProgress(scrollBehavior.state)

    PullToRefreshBox(
        enabled = expansionProgress == 1f,
        onRefresh = { onEvent(GravatarEvent.Refresh()) },
        isRefreshing = uiState.isRefreshing,
        modifier = Modifier
    ) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CollapsibleTopAppBar(
                    minVisibleHeight = GRAVATAR_HEADER_COLLAPSED_HEIGHT,
                    scrollBehavior = scrollBehavior,
                ) {
                    GravatarHeader(
                        uiState.avatarUrl,
                        modifier = Modifier.fillMaxWidth(),
                        progress = expansionProgress,
                        onAboutAppClicked = {
                            onEvent(GravatarEvent.OnAboutAppClicked)
                        }
                    )
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                val gridState = rememberLazyGridState()
                val contentPadding = PaddingValues(16.dp)
                val itemSpacing = 2.dp

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
                            onTakePictureClicked = onTakePictureClicked,
                            onChooseFromGalleryClicked = onPickMediaClicked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                        )
                    }
                    when {
                        uiState.isLoading -> {
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
                        }

                        uiState.avatars == null -> {
                            item(
                                span = { GridItemSpan((maxLineSpan)) }
                            ) {
                                ErrorViewWithRetry(
                                    errorTitle = stringResource(R.string.gravatar_tab_unable_to_load_avatars),
                                    errorMessage = stringResource(R.string.gravatar_tab_unable_to_load_avatars_message),
                                    onRetryClicked = { onEvent(GravatarEvent.Refresh(false)) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp)
                                )
                            }
                        }

                        else -> {
                            avatarsGridSection(
                                avatars = uiState.avatarsUi.orEmpty(),
                                onAvatarOptionClicked = { avatar, option ->
                                    when (option) {
                                        AvatarOption.Select -> {
                                            onEvent(GravatarEvent.OnAvatarSelected(avatar.imageId))
                                        }

                                        AvatarOption.Delete -> {
                                            onEvent(GravatarEvent.OnShowDeleteConfirmation(avatar.imageId))
                                        }

                                        AvatarOption.Download -> {
                                            permissionAwareDownloadImageCallback(avatar)
                                        }
                                    }
                                },
                                onFailedAvatarClicked = { uri ->
                                    onEvent(GravatarEvent.OnFailedAvatarTapped(uri))
                                }
                            )
                        }
                    }
                }
            }
            FailedAvatarUploadAlertDialog(
                avatarUploadFailure = uiState.failedUploadDialog,
                onRemoveUploadClicked = { onEvent(GravatarEvent.OnFailedAvatarDismissed(it)) },
                onRetryClicked = { onEvent(GravatarEvent.OnImageCropped(it)) },
                onDismiss = { onEvent(GravatarEvent.OnFailedAvatarDialogDismissed) },
            )
            PermissionRationaleDialog(
                isVisible = storagePermissionRationaleDialogVisible,
                message = stringResource(R.string.permission_required_write_external_storage_rationale_message),
                onConfirmation = {
                    storagePermissionRationaleDialogVisible = false
                    context.openAppPermissionSettings()
                },
                onDismiss = { storagePermissionRationaleDialogVisible = false },
            )
            uiState.confirmAvatarDeletionId?.let {
                AvatarDeletionConfirmationDialog(
                    onConfirm = { onEvent(GravatarEvent.OnDeleteAvatar(it)) },
                    onDismiss = { onEvent(GravatarEvent.OnDismissDeleteConfirmation) },
                )
            }
        }
        if (uiState.isAboutAppDialogVisible) {
            AboutAppDialog(
                onDismissRequest = { onEvent(GravatarEvent.OnDismissAboutAppDialog) },
            )
        }
    }
}

@Suppress("LongMethod")
private fun GravatarAction.handle(
    context: Context,
    uCropLauncher: ActivityResultLauncher<Intent>,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
) {
    when (this) {
        is GravatarAction.LaunchImageCropper -> {
            uCropLauncher.launchUCrop(
                context = context,
                targetImageUri = this.imageUri,
                currentImageFile = this.tempFile
            )
        }

        GravatarAction.AvatarSelected -> {
            scope.launch {
                snackbarHostState.showGravatarSnackbar(
                    message = context.getString(R.string.gravatar_tab_avatar_updated_successfully),
                    withDismissAction = true,
                )
            }
        }

        GravatarAction.AvatarSelectionFailed -> {
            scope.launch {
                snackbarHostState.showGravatarSnackbar(
                    message = context.getString(R.string.gravatar_tab_avatar_selection_failed),
                    withDismissAction = true,
                    snackbarType = SnackbarType.Error,
                )
            }
        }

        GravatarAction.AvatarDeletionFailed -> {
            scope.launch {
                snackbarHostState.showGravatarSnackbar(
                    message = context.getString(R.string.gravatar_tab_avatar_deletion_failed),
                    withDismissAction = true,
                    snackbarType = SnackbarType.Error,
                )
            }
        }

        GravatarAction.DownloadManagerNotAvailable -> {
            scope.launch {
                snackbarHostState.showGravatarSnackbar(
                    message = context.getString(R.string.gravatar_tab_download_manager_not_available),
                    withDismissAction = true,
                    snackbarType = SnackbarType.Error,
                )
            }
        }

        GravatarAction.AvatarDownloadStarted -> {
            scope.launch {
                snackbarHostState.showGravatarSnackbar(
                    message = context.getString(R.string.gravatar_tab_avatar_download_started),
                    withDismissAction = true,
                )
            }
        }

        is GravatarAction.OpenExternalUrl -> {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        }

        is GravatarAction.ShareProfileUrl -> {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, url)
                type = "text/plain"
            }
            val chooserIntent = Intent.createChooser(shareIntent, null)
            context.startActivity(chooserIntent)
        }
    }
}

private fun ActivityResultLauncher<Intent>.launchUCrop(
    context: Context,
    targetImageUri: Uri,
    currentImageFile: File,
) {
    val options = UCrop.Options().apply {
        setToolbarColor(Color.BLACK)
        setStatusBarColor(Color.BLACK)
        setToolbarWidgetColor(Color.WHITE)
        setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.NONE)
        setCompressionQuality(UCROP_COMPRESSION_QUALITY)
        withMaxResultSize(UCROP_MAX_IMAGE_SIZE, UCROP_MAX_IMAGE_SIZE)
        setCircleDimmedLayer(true)
    }
    launch(
        UCrop.of(targetImageUri, Uri.fromFile(currentImageFile))
            .withAspectRatio(1f, 1f)
            .withOptions(options)
            .getIntent(context)
    )
}

private const val UCROP_COMPRESSION_QUALITY = 75
private const val UCROP_MAX_IMAGE_SIZE = 1080

@PreviewLightDark
@Composable
private fun GravatarScreenPreview() {
    GravatarAppTheme {
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
            onTakePictureClicked = { },
            onPickMediaClicked = { },
        )
    }
}
