package kky.flab.lookaround.core.ui.component.calendar

import androidx.compose.runtime.Stable
import java.time.LocalDate

@Stable
data class DiaryEventData(
    val data: List<LocalDate>
) {
    fun eventCount(date: LocalDate): Int = data.count { it == date }
}
