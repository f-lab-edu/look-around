package kky.flab.lookaround.core.ui.component.calendar

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.YearMonth
import java.util.GregorianCalendar

@Immutable
data class CalendarState(
    val year: Int,
    val month: Int,
) {
    val weeks: List<List<CalendarDate>>

    private val firstDayOfWeek: Int
    private val lastDayOfWeek: Int
    private val lastDayOfMonth: Int
    private val weekCount: Int

    private val gregorian: GregorianCalendar

    init {
        val yearMonth = YearMonth.of(year, month)
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()

        gregorian = GregorianCalendar().apply {
            set(GregorianCalendar.YEAR, year)
            set(GregorianCalendar.MONTH, month)
            set(GregorianCalendar.DAY_OF_MONTH, 1)
        }

        fun actualDayOfWeek(localDayOfWeek: Int) =
            if (localDayOfWeek == 7) {
                1
            } else {
                localDayOfWeek + 1
            }

        firstDayOfWeek = actualDayOfWeek(firstDay.dayOfWeek.value)
        lastDayOfWeek = actualDayOfWeek(lastDay.dayOfWeek.value)
        lastDayOfMonth = lastDay.dayOfMonth

        val startOffset = firstDayOfWeek - 1
        val lastOffset = 7 - lastDayOfWeek
        val listSize = lastDayOfMonth + startOffset + lastOffset

        val sum = firstDayOfWeek - 1 + lastDayOfMonth
        weekCount = if (sum % 7 == 0) {
            sum / 7
        } else {
            sum / 7 + 1
        }

        //이전 달 날짜, 다음 달 날짜를 포함한 List를 만든다.
        weeks = List(listSize) { index ->
            val date = when {
                index + 1 <= startOffset -> firstDay.minusDays((startOffset - index).toLong())
                index + 1 > lastDayOfMonth + startOffset -> lastDay.plusDays(
                    ((index + 1) - (startOffset + lastDayOfMonth)).toLong()
                )
                else -> firstDay.plusDays((index - startOffset).toLong())
            }
            CalendarDate(
                date = date,
                isCurrentMonth = date.monthValue == month,
            )
        }.chunked(7)
    }

    companion object {
        fun of(gregorianCalendar: GregorianCalendar): CalendarState {
            return CalendarState(
                year = gregorianCalendar.get(GregorianCalendar.YEAR),
                month = gregorianCalendar.get(GregorianCalendar.MONTH),
            )
        }

        fun of(date: LocalDate): CalendarState {
            return CalendarState(
                year = date.year,
                month = date.monthValue,
            )
        }

        fun now(
            selected: List<LocalDate> = emptyList()
        ): CalendarState {
            val yearMonth = YearMonth.now()
            return CalendarState(
                year = yearMonth.year,
                month = yearMonth.monthValue,
            )
        }
    }
}
