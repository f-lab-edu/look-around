package kky.flab.lookaround.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LookaroundTopBar(
    title: String? = null,
    navigationType: NavigationType = NavigationType.None,
    onNavigationClick: () -> Unit = {},
) {
    val appbarSize = 56.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(appbarSize),
    ) {
        if (navigationType == NavigationType.Back) {
            IconButton(
                onClick = onNavigationClick,
                modifier = Modifier
                    .size(appbarSize)
                    .align(Alignment.CenterStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        if (navigationType == NavigationType.Close) {
            IconButton(
                onClick = onNavigationClick,
                modifier = Modifier
                    .size(appbarSize)
                    .align(Alignment.CenterEnd)
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (title != null) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                ),
                modifier = Modifier
                    .padding(
                        start =
                        if (navigationType == NavigationType.Back) 0.dp
                        else 16.dp
                    )
                    .align(Alignment.CenterStart)
            )
        }
    }
}

enum class NavigationType {
    None,
    Back,
    Close;
}
