package kky.flab.lookaround.feature.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kky.flab.lookaround.core.ui.component.LookaroundCard
import kky.flab.lookaround.core.ui.component.calendar.CalendarState
import kky.flab.lookaround.core.ui.component.calendar.DiaryEventData
import kky.flab.lookaround.core.ui.component.calendar.LaDiaryCalendar
import kky.flab.lookaround.core.ui.component.calendar.Language
import java.time.LocalDate
import kky.flab.lookaround.core.ui.R as CommonUiResource

@Composable
fun CalendarCard(
    modifier: Modifier = Modifier,
    selected: List<LocalDate>,
    onClickDate: (LocalDate) -> Unit,
) {
    LookaroundCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
            ) {
                Image(
                    painter = painterResource(CommonUiResource.drawable.img_calendar),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(18.dp)
                )
                Text(
                    text = "이번 달 산책",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                )
            }
            LaDiaryCalendar (
                language = Language.KOR,
                calendarState = CalendarState.now(selected),
                event = DiaryEventData(selected),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClickDate = onClickDate,
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun CalendarCardPreview() {
    CalendarCard(
        selected = emptyList()
    ) { }
}