package kky.flab.lookaround.feature.main.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainBottomNavigation(
    modifier: Modifier = Modifier,
    tabs: MainBottomNavigationScope.() -> Unit,
) {
    val scope = remember(tabs) { MainBottomNavigationScopeImpl().apply(tabs) }

    Surface(
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.background,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for(tab in scope.tabs) {
                tab.invoke(this)
            }
        }
    }
}
