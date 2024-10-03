@file:OptIn(ExperimentalMaterial3Api::class)

package kky.flab.lookaround.feature.recording

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.PathOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import kky.flab.lookaround.core.ui.component.LookaroundAlertDialog
import kky.flab.lookaround.core.ui.util.millsToTimeFormat
import kky.flab.lookaround.feature.recording.component.RecordingInfoRow
import kky.flab.lookaround.feature.recording.model.RecordingEffect
import kky.flab.lookaround.feature.recording.model.RecordingUiState
import kotlinx.coroutines.launch

@Composable
fun RecordingScreen(
    viewModel: RecordingViewModel = hiltViewModel(),
    askFinish: Boolean,
    onFinish: () -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val time by viewModel.timer.collectAsStateWithLifecycle(initialValue = 0)

    var windowInit by remember { mutableStateOf(false) }

    var showRecordingFinishDialog by remember { mutableStateOf(askFinish) }

    if (showRecordingFinishDialog) {
        LookaroundAlertDialog(
            text = stringResource(R.string.end_recording_message),
            onDismiss = { showRecordingFinishDialog = false },
            onConfirm = { viewModel.complete() }
        )
    }

    if (!windowInit) {
        val view = LocalView.current
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
        windowInit = true
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                is RecordingEffect.SavedRecording -> onFinish()
            }
        }
    }

    RecordingScreen(
        uiState = uiState,
        time = time,
        onShowFinishDialog = {
            showRecordingFinishDialog = true
        },
    )
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalNaverMapApi::class)
@Composable
internal fun RecordingScreen(
    uiState: RecordingUiState,
    time: Long,
    onShowFinishDialog: () -> Unit,
) {
    val timeText = time.millsToTimeFormat()
    val mapCameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        modifier = Modifier.navigationBarsPadding(),
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetShape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
        ),
        sheetPeekHeight = 112.dp, //BottomSheet navigationBarsPadding 적용 안되는 문제
        sheetContent = {
            Column(
                modifier = Modifier.navigationBarsPadding()
            ) {
                RecordingInfoRow(
                    text = timeText,
                    image = painterResource(
                        kky.flab.lookaround.core.ui.R.drawable.img_stopwatch
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                RecordingInfoRow(
                    text = "${uiState.distance}m",
                    image = painterResource(
                        kky.flab.lookaround.core.ui.R.drawable.img_person_walking
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 10.dp)
                )
                TextButton(
                    onClick = onShowFinishDialog,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 16.dp,
                        )
                ) {
                    Text(
                        text = "종료하기",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        },
    ) {
        NaverMap(
            cameraPositionState = mapCameraPositionState,
            onMapLoaded = {
                val locationProviderClient =
                    LocationServices.getFusedLocationProviderClient(context)
                locationProviderClient.lastLocation.addOnSuccessListener {
                    it?.let {
                        val cameraUpdate =
                            CameraUpdate.scrollAndZoomTo(
                                LatLng(it.latitude, it.longitude),
                                17.0
                            )
                        scope.launch {
                            mapCameraPositionState.animate(
                                cameraUpdate,
                                animation = CameraAnimation.None
                            )
                        }
                    }
                }
            }
        ) {
            if (uiState.path.size > 1) {
                PathOverlay(
                    coords = uiState.path.map { LatLng(it.latitude, it.longitude) },
                    color = MaterialTheme.colorScheme.primary,
                    passedColor = MaterialTheme.colorScheme.primary,
                    outlineWidth = 1.dp,
                    outlineColor = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun RecordingScreenPreview() {
    RecordingScreen(
        uiState = RecordingUiState(
            path = emptyList(),
            distance = 35,
        ),
        time = 6000,
        onShowFinishDialog = {}
    )
}
