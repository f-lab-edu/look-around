package kky.flab.lookaround.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kky.flab.lookaround.core.domain.const.SummaryFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSummaryFilterBottomSheet(
    onDismiss: () -> Unit,
    onSelectFilter: (SummaryFilter) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier.navigationBarsPadding()
        ) {
            for (filter in SummaryFilter.entries) {
                Text(
                    text = when (filter) {
                        SummaryFilter.WEEK -> "일주일"
                        SummaryFilter.MONTH -> "한달"
                        SummaryFilter.YEAR -> "1년"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .clickable {
                            onSelectFilter(filter)
                            onDismiss()
                        },
                )
                HorizontalDivider(thickness = 1.dp)
            }
        }
    }
}