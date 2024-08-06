package kky.flab.lookaround.feature.recording.model

sealed interface ModifyRecordEffect {
    data object SaveRecord: ModifyRecordEffect

    data class Error(val message: String): ModifyRecordEffect
}
