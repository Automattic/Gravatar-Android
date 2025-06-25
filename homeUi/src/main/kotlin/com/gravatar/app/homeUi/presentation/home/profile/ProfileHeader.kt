package com.gravatar.app.homeUi.presentation.home.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.extensions.defaultProfile
import com.gravatar.restapi.models.Profile
import com.gravatar.ui.components.atomic.Avatar

@Composable
internal fun ProfileHeader(
    profile: Profile,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            profile = profile,
            size = 44.dp,
            modifier = Modifier.clip(CircleShape),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = profile.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            profile.jobInfo().takeIf { it.isNotBlank() }?.let { jobInfo ->
                Text(
                    text = jobInfo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

private fun Profile.jobInfo(): String {
    return buildString {
        if (jobTitle.isNotBlank()) {
            append(jobTitle)
        }
        if (company.isNotBlank()) {
            if (isNotEmpty()) append(", ")
            append(company)
        }
    }
}

@Preview(showBackground = true, name = "Both job title and company")
@Composable
fun ProfileHeaderPreviewBoth() {
    ProfileHeader(
        profile = defaultProfile(
            hash = "",
            displayName = "John Doe",
            jobTitle = "Software Engineer",
            company = "Automattic"
        ),
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true, name = "Only job title")
@Composable
fun ProfileHeaderPreviewJobOnly() {
    ProfileHeader(
        profile = defaultProfile(hash = "", displayName = "John Doe", jobTitle = "Software Engineer", company = ""),
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true, name = "Only company")
@Composable
fun ProfileHeaderPreviewCompanyOnly() {
    ProfileHeader(
        profile = defaultProfile(hash = "", displayName = "John Doe", jobTitle = "", company = "Automattic"),
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true, name = "Neither job title nor company")
@Composable
fun ProfileHeaderPreviewNeither() {
    ProfileHeader(
        profile = defaultProfile(hash = "", displayName = "John Doe", jobTitle = "", company = ""),
        modifier = Modifier.padding(16.dp)
    )
}
