package kky.flab.lookaround.feature.home.model

sealed interface Effect {
    data object ShowStartRecordingMessage: Effect

    data object ShowEndRecordingMessage: Effect

    data object StartRecordingService: Effect

    data class Error(
        val message: String
    ): Effect
}
