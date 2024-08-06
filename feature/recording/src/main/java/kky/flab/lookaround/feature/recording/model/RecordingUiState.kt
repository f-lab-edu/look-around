package kky.flab.lookaround.feature.recording.model

import com.naver.maps.geometry.LatLng

data class RecordingUiState(
    val path: List<LatLng>,
    val distance: Long,
) {
    companion object {
        val EMPTY = RecordingUiState(
            path = emptyList(),
            distance = 0,
        )
    }
}
