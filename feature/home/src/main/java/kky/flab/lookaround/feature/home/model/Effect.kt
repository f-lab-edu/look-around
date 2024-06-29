package kky.flab.lookaround.feature.home.model

internal sealed interface Effect {
    val message: String
}

internal data class ShowStartRecordingMessage(
    override val message: String = "산책을 시작해볼까요?"
): Effect

internal data class ShowEndRecordingMessage(
    override val message: String = "산책을 종료할까요?"
): Effect

internal data class Error(
    override val message: String
): Effect