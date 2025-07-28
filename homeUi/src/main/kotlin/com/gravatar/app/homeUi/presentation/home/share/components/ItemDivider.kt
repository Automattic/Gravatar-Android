package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun ItemDivider(modifier: Modifier = Modifier) {
    Column(modifier) {
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(8.dp))
    }
}
