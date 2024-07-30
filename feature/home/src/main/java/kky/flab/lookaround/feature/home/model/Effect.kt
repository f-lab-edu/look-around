package kky.flab.lookaround.feature.home.model

internal sealed interface Effect {
    data object ShowStartRecordingMessage: Effect

    data object ShowEndRecordingMessage: Effect

    data object StartRecordingService: Effect

    data object StopRecordingService: Effect

    data class Error(
        val message: String
    ): Effect
}
