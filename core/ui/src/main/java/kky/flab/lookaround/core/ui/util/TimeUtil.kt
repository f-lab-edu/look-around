package kky.flab.lookaround.core.ui.util

import java.util.concurrent.TimeUnit

fun Long.millsToTimeFormat(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60

    val hourString = hours.toString().padStart(2, '0')
    val minutesString = minutes.toString().padStart(2, '0')
    val secondsString = seconds.toString().padStart(2, '0')

    val result =
        if (hours > 0) "${hourString}:${minutesString}:${secondsString}"
        else "${minutesString}:${secondsString}"

    return result
}