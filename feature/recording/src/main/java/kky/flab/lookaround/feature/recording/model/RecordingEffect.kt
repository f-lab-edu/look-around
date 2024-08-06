package kky.flab.lookaround.feature.recording.model

sealed interface RecordingEffect {
    data class SavedRecording(
        val id: Long,
    ): RecordingEffect
}
