package kky.flab.lookaround.core.ui.component.calendar

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class CalendarDate(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
)
