package kky.flab.lookaround.feature.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kky.flab.lookaround.core.domain.const.SummaryFilter
import kky.flab.lookaround.core.domain.model.Summary
import kky.flab.lookaround.core.domain.model.Weather
import kky.flab.lookaround.core.ui.theme.LookaroundTheme
import kky.flab.lookaround.core.ui.util.getAddress
import kky.flab.lookaround.core.ui.util.xlsx.ParseResult
import kky.flab.lookaround.core.ui.util.xlsx.XlsxParser
import kky.flab.lookaround.feature.home.component.DialogType
import kky.flab.lookaround.feature.home.component.HomeSummaryFilterBottomSheet
import kky.flab.lookaround.feature.home.component.LocationPermissionDialog
import kky.flab.lookaround.feature.home.component.RecordingStateCard
import kky.flab.lookaround.feature.home.component.StartRecordDialog
import kky.flab.lookaround.feature.home.component.SummaryCard
import kky.flab.lookaround.feature.home.component.WeatherCard
import kky.flab.lookaround.feature.home.model.Effect
import kky.flab.lookaround.feature.home.model.SummaryUiState
import kky.flab.lookaround.feature.home.model.UiState
import kky.flab.lookaround.feature.home.model.WeatherUiState
import kky.flab.lookaround.feature.home.service.RecordService
import kky.flab.lookaround.feature.recording.RecordingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()

    var showAlertDialog by remember { mutableStateOf(DialogType.Dismiss) }

    var showSummaryFilterBottomSheet by remember { mutableStateOf(false) }

    fun parseApiData(
        onParsed: (ParseResult) -> Unit
    ) {
        scope.launch {
            val address = getAddress(context) ?: return@launch
            val parseResult = withContext(Dispatchers.IO) {
                XlsxParser.findXY(context, address)
            }

            onParsed(parseResult)
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val isGranted = result.values.all { it }
            if (isGranted) {
                parseApiData { parseResult ->
                    viewModel.loadWeather(parseResult.nx, parseResult.ny)
                }
            } else {
                viewModel.onDenyPermissionForWeather()
            }
        }

    val recordPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                showAlertDialog = DialogType.StartRecord
            } else {
                /* 스낵바 */
            }
        }

    when (showAlertDialog) {
        DialogType.Dismiss -> {}

        DialogType.LoadWeatherPermission -> LocationPermissionDialog(
            onDismiss = { showAlertDialog = DialogType.Dismiss },
            onConfirm = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)

                showAlertDialog = DialogType.Dismiss
            }
        )

        DialogType.StartRecord -> StartRecordDialog(
            onDismiss = { showAlertDialog = DialogType.Dismiss },
            onConfirm = {
                viewModel.startRecording()
                showAlertDialog = DialogType.Dismiss
            }
        )
    }

    if (showSummaryFilterBottomSheet) {
        HomeSummaryFilterBottomSheet(
            onDismiss = { showSummaryFilterBottomSheet = false },
            onSelectFilter = { filter ->
                viewModel.changeSummaryFilter(filter)
            }
        )
    }

    if (state.initializedConfig) {
        LaunchedEffect(Unit) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                is Effect.Error -> { /* 스낵바 */ }
                Effect.ShowEndRecordingMessage -> TODO()
                Effect.ShowStartRecordingMessage -> TODO()
                Effect.StartRecordingService -> {
                    context.startForegroundService(
                        Intent(
                            context,
                            RecordService::class.java
                        )
                    )

                    context.startActivity(Intent(context, RecordingActivity::class.java))
                }
            }
        }
    }

    HomeScreen(
        state = state,
        onStartWalking = {
            if (state.recording) {
                context.startActivity(Intent(context, RecordingActivity::class.java))
            } else {
                recordPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        },
        onWeatherRetry = {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        },
        onClickFilter = {
            showSummaryFilterBottomSheet = true
        }
    )
}

@Composable
fun HomeScreen(
    state: UiState,
    onStartWalking: () -> Unit,
    onWeatherRetry: () -> Unit,
    onClickFilter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        RecordingStateCard(
            recording = state.recording,
            onStartWalking = onStartWalking,
        )
        Spacer(modifier = Modifier.height(16.dp))
        WeatherCard(
            uiState = state.weatherUiState,
            onRetry = onWeatherRetry
        )
        Spacer(modifier = Modifier.height(16.dp))
        SummaryCard(
            state = state.summaryUiState,
            filter = when(state.summaryFilter) {
                SummaryFilter.WEEK -> "일주일"
                SummaryFilter.MONTH -> "한달"
                SummaryFilter.YEAR -> "1년"
            },
            onClickFilter = onClickFilter
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun HomeScreenPreview() {
    LookaroundTheme {
        HomeScreen(
            state = UiState(
                recording = true,
                initializedConfig = true,
                summaryFilter = SummaryFilter.WEEK,
                weatherUiState = WeatherUiState.Result(
                    Weather(
                        sky = Weather.Sky.SUNNY,
                        precipitation = 1.0,
                        windSpeed = 15,
                        temperatures = 27,
                        time = "",
                        thunderStroke = 0.0,
                    )
                ),
                summaryUiState = SummaryUiState.Result(
                    Summary(
                        time = 4000,
                        count = 20,
                        startTime = 0,
                        endTime = 0,
                        mostDayOfWeek = "월"
                    )
                ),
            ),
            onStartWalking = {},
            onWeatherRetry = {},
            onClickFilter = {},
        )
    }
}
