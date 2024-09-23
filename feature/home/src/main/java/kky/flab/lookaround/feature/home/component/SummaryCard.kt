package kky.flab.lookaround.feature.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kky.flab.lookaround.core.ui.component.LookaroundCard
import kky.flab.lookaround.feature.home.R
import kky.flab.lookaround.feature.home.model.SummaryUiState
import java.util.concurrent.TimeUnit

@Composable
fun SummaryCard(
    state: SummaryUiState,
    filter: String,
    onClickFilter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LookaroundCard(modifier = modifier.fillMaxWidth()) {
        when (state) {
            SummaryUiState.Empty -> {}
            SummaryUiState.Loading -> LoadingBox()
            is SummaryUiState.Result -> SummaryContent(
                filter = filter,
                state = state,
                onClickFilter = onClickFilter,
            )
        }
    }
}

@Composable
fun SummaryContent(
    filter: String,
    state: SummaryUiState.Result,
    onClickFilter: () -> Unit,
) {
    val time = StringBuilder().apply {
        val data = state.summary.time
        val hour = TimeUnit.MILLISECONDS.toHours(data)
        if (hour > 0) {
            append("${hour}시간")
        }
        val minutes = TimeUnit.MILLISECONDS.toMinutes(data) % 60
        if (minutes > 0) {
            append("${minutes}분")
        }
    }.toString()

    val count = "${state.summary.count}회"
    val mostOfWeek = "${state.summary.mostDayOfWeek}요일"

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onClickFilter),
        ) {
            Text(
                text = filter,
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                painter = painterResource(R.drawable.baseline_tune_24),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = annotatedString(prefix = "이 기간 동안 산책 ", target = count),
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = annotatedString(prefix = "총 산책한 시간은 ", target = time),
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = annotatedString(prefix = "산책을 가장 자주 한 날은 ", target = mostOfWeek),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun annotatedString(
    prefix: String,
    target: String,
    targetColor: Color = MaterialTheme.colorScheme.primary
) = buildAnnotatedString {
    append(prefix)
    withStyle(
        SpanStyle(
            fontWeight = FontWeight.Bold,
            color = targetColor
        )
    ) {
        append(target)
    }
}
