package kky.flab.lookaround.feature.home.model

import com.naver.maps.geometry.LatLng
import kky.flab.lookaround.core.domain.model.Path

data class RecordingState(
    val path: List<LatLng>,
    val distance: Long,
) {
    companion object {
        val EMPTY = RecordingState(
            path = emptyList(),
            distance = 0,
        )
    }
}