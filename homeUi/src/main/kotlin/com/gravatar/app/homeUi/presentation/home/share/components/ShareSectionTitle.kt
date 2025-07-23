package com.gravatar.app.homeUi.presentation.home.share.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravatar.app.design.theme.GravatarAppTheme
import com.gravatar.app.homeUi.R

@Composable
internal fun ShareSectionTitle(
    @DrawableRes leftIcon: Int?,
    @StringRes title: Int,
    @DrawableRes rightIcon: Int?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        leftIcon?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(end = 6.dp)
            )
        }
        BasicText(
            text = stringResource(title),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(
                maxFontSize = 16.sp
            ),
            modifier = Modifier.weight(1f),
        )
        rightIcon?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShareSectionTitlePreview() {
    GravatarAppTheme {
        ShareSectionTitle(
            leftIcon = R.drawable.share_section_title_warning,
            title = R.string.share_tab_private_contact_info_title,
            rightIcon = R.drawable.share_section_title_lock,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
