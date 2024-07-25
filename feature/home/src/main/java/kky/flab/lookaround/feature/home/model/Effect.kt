package kky.flab.lookaround.feature.home.model

internal sealed interface Effect {
    val message: String
    data class ShowStartRecordingMessage(
        override val message: String = "산책을 시작해볼까요?"
    ): Effect

    data class ShowEndRecordingMessage(
        override val message: String = ""
    ): Effect

    data object StartRecordingService: Effect {
        override val message: String
            get() = ""
    }

    data object StopRecordingService: Effect {
        override val message: String
            get() = ""
    }

    data class Error(
        override val message: String
    ): Effect
}
