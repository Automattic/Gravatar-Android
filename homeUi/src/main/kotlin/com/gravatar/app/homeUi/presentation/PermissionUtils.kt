package com.gravatar.app.homeUi.presentation

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal fun Context.openAppPermissionSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.setData(uri)
    startActivity(intent)
}

internal fun Context.withPermission(
    permission: String,
    onRequestPermission: (String) -> Unit,
    onShowRationale: () -> Unit = {},
    grantedCallback: () -> Unit,
) {
    val activity = findComponentActivity()
    when {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
            grantedCallback()
        }

        activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
            onShowRationale()
        }

        else -> {
            onRequestPermission(permission)
        }
    }
}

internal fun Context.findComponentActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findComponentActivity()
    else -> null
}
