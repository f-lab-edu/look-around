package kky.flab.lookaround.feature.main.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

interface MainBottomNavigationScope {
    fun bottomNavigationItem(
        @DrawableRes iconDrawableResId: Int,
        label: String,
        selected: Boolean,
        onClick: () -> Unit,
    )
}

internal class MainBottomNavigationScopeImpl : MainBottomNavigationScope {
    val tabs = mutableListOf<@Composable RowScope.() -> Unit>()

    override fun bottomNavigationItem(
        iconDrawableResId: Int,
        label: String,
        selected: Boolean,
        onClick: () -> Unit,
    ) {
        tabs += {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1.0f).clickable(onClick = onClick)
            ) {
                val iconTint =
                    if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline

                val labelColor =
                    if (selected) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.surfaceVariant

                Icon(
                    painter = painterResource(iconDrawableResId),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor,
                )
            }
        }
    }
}
