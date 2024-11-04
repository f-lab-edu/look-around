package kky.flab.lookaround.core.ui.component.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun LaDiaryCalendar(
    event: DiaryEventData,
    modifier: Modifier = Modifier,
    language: Language = Language.KOR,
    calendarState: CalendarState = CalendarState.now(),
    onClickDate: (LocalDate) -> Unit,
) {
    CalendarContent(
        state = calendarState,
        language = language,
        modifier = modifier,
        onClickDate = onClickDate,
        decoration = { calendarDate ->
            val eventCount = event.eventCount(calendarDate.date)
            val color = when(eventCount) {
                1 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                2 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                3,4,5,6,7,8,9,10 -> MaterialTheme.colorScheme.primary
                else -> Color.Transparent
            }

            Box(
                Modifier
                    .background(
                        color = color,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .aspectRatio(1f)
                    .align(Alignment.Center)
            )
        }
    )
}

@Composable
private fun CalendarContent(
    state: CalendarState,
    modifier: Modifier = Modifier,
    language: Language = Language.KOR,
    onClickDate: (LocalDate) -> Unit,
    decoration: @Composable BoxScope.(CalendarDate) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        WeekHeader(language = language)
        for (week in state.weeks) {
            Row {
                for (date in week) {
                    LaCalendarDay(
                        date = date,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.5f),
                        enable = date.isCurrentMonth,
                        onClickDate = { onClickDate(date.date) },
                        decoration = decoration
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekHeader(
    language: Language
) {
    Row {
        for (dayOfWeek in DayOfWeek.entries) {
            val dayOfWeekValue = when (language) {
                Language.KOR -> dayOfWeek.kor
                Language.ENG -> dayOfWeek.eng
            }
            Text(
                text = dayOfWeekValue,
                color = if (dayOfWeek == DayOfWeek.SUN) {
                    Color.Red
                } else {
                    Color.Unspecified
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1.5f),
            )
        }
    }
}

@Composable
private fun LaCalendarDay(
    date: CalendarDate,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    onClickDate: (CalendarDate) -> Unit,
    decoration: @Composable BoxScope.(CalendarDate) -> Unit,
) {
    Box(
        modifier = modifier.clickable { onClickDate(date) },
    ) {
        decoration(date)
        DateText(
            date = date,
            textStyle = MaterialTheme.typography.bodySmall.copy(
                color = if (enable) MaterialTheme.colorScheme.onSurface
                else Color.Gray
            ),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
fun DateText(
    date: CalendarDate,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default.copy(fontSize = 12.sp)
) {
    Text(
        text = date.date.dayOfMonth.toString(),
        style = textStyle,
        modifier = modifier
    )
}

enum class Language {
    KOR,
    ENG,
}

enum class DayOfWeek(
    val kor: String,
    val eng: String,
) {
    SUN("일", "SUN"),
    MON("월", "MON"),
    TUE("화", "TUE"),
    WED("수", "WED"),
    THU("목", "THU"),
    FRI("금", "FRI"),
    SAT("토", "SAT"),
}
