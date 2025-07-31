package com.gravatar.app.homeUi.presentation.home.share

import java.io.File

internal sealed class ShareAction {
    data class ShareVCard(val vCardFile: File) : ShareAction()
    data class ShowBottomBar(val show: Boolean) : ShareAction()
}
