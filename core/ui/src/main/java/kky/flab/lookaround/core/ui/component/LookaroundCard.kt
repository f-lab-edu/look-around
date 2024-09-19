package kky.flab.lookaround.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LookaroundCard(
    modifier: Modifier = Modifier,
    radius: Int = 10,
    elevation: Int = 2,
    color: Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Surface(
        shadowElevation = elevation.dp,
        shape = RoundedCornerShape(radius.dp),
        color = color,
        modifier = modifier.clickable { onClick?.invoke() },
        content = content,
    )
}
