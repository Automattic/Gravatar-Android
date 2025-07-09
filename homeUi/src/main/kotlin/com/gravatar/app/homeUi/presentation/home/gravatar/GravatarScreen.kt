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
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.gravatar.app.homeUi.GravatarFileProvider
import com.gravatar.app.homeUi.R
import com.gravatar.app.homeUi.presentation.home.components.PermissionRationaleDialog
import com.gravatar.app.homeUi.presentation.home.gravatar.components.AvatarDeletionConfirmationDialog
import com.gravatar.app.homeUi.presentation.home.gravatar.components.AvatarOption
import com.gravatar.app.homeUi.presentation.home.gravatar.components.FailedAvatarUploadAlertDialog
import com.gravatar.app.homeUi.presentation.home.gravatar.components.GravatarHeader
import com.gravatar.app.homeUi.presentation.home.gravatar.components.UploadNewAvatarSection
import com.gravatar.app.homeUi.presentation.home.gravatar.components.avatarSize
import com.gravatar.app.homeUi.presentation.home.gravatar.components.avatarsGridSection
import com.gravatar.app.homeUi.presentation.openAppPermissionSettings
import com.gravatar.app.homeUi.presentation.withPermission
import com.gravatar.app.usercomponent.domain.usecase.Logout
import com.gravatar.restapi.models.Avatar
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.io.File
import java.net.URI

@Composable
internal fun GravatarScreen(
    viewModel: GravatarViewModel = koinViewModel()
) {
    val logout = koinInject<Logout>()
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
                    when (action) {
                        is GravatarAction.LaunchImageCropper -> {
                            uCropLauncher.launchUCrop(
                                context = context,
                                targetImageUri = action.imageUri,
                                currentImageFile = action.tempFile
                            )
                        }
                    }
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
        onMenuClick = {
            scope.launch { logout() }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GravatarScreen(
    uiState: GravatarUiState,
    onMenuClick: () -> Unit = {},
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
            Column {
                GravatarHeader(
                    uiState.avatarUrl,
                    modifier = Modifier.fillMaxWidth(),
                    onMenuClick = onMenuClick,
                )
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
        onTakePictureClicked = { },
        onPickMediaClicked = { },
    )
}
