package kky.flab.lookaround.feature.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.internal.managers.FragmentComponentManager
import kky.flab.lookaround.core.domain.const.SummaryFilter
import kky.flab.lookaround.core.domain.model.Summary
import kky.flab.lookaround.core.domain.model.Weather
import kky.flab.lookaround.core.ui.component.LookaroundAlertDialog
import kky.flab.lookaround.core.ui.theme.LookaroundTheme
import kky.flab.lookaround.core.ui.util.getAddress
import kky.flab.lookaround.core.ui.util.xlsx.XlsxParser
import kky.flab.lookaround.feature.home.component.HomeSummaryFilterBottomSheet
import kky.flab.lookaround.feature.home.component.RecordingStateCard
import kky.flab.lookaround.feature.home.component.SummaryCard
import kky.flab.lookaround.feature.home.component.WeatherCard
import kky.flab.lookaround.feature.home.model.Effect
import kky.flab.lookaround.feature.home.model.SummaryUiState
import kky.flab.lookaround.feature.home.model.UiState
import kky.flab.lookaround.feature.home.model.WeatherUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val homeScreenPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onStartRecordingService: () -> Unit,
    onRouteRecording: () -> Unit,
    onShowSnackBar: (String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()

    var showAlertDialog by remember { mutableStateOf(HomeDialogType.Dismiss) }

    var showSummaryFilterBottomSheet by remember { mutableStateOf(false) }

    var askedLocationPermission by rememberSaveable { mutableStateOf(false) }

    fun loadWeather() {
        viewModel.updateWeatherStateLoading()
        scope.launch {
            runCatching {
                val address = getAddress(context) ?: return@launch
                val parseResult = withContext(Dispatchers.IO) {
                    XlsxParser.findXY(context, address)
                }

                parseResult
            }.onFailure { cause ->
                viewModel.updateWeatherStateLocationFail(cause)
            }.onSuccess { parseResult ->
                viewModel.loadWeather(parseResult.nx, parseResult.ny)
            }
        }
    }

    fun Array<String>.checkPermission(): Boolean = this
        .map { ContextCompat.checkSelfPermission(context, it) }
        .all { result -> result == PackageManager.PERMISSION_GRANTED }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            if (result.isEmpty()) return@rememberLauncherForActivityResult

            val isGranted = result.values.all { it }
            if (isGranted) {
                loadWeather()
            } else {
                viewModel.onDenyPermissionForWeather()
            }
        }

    val recordPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                showAlertDialog = HomeDialogType.StartRecord
            } else {
                onShowSnackBar("서비스를 이용하기 위해 권한을 허용해주세요.")
            }
        }

    LaunchedEffect(state.initializedConfig) {
        if (state.initializedConfig) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            val granted = permissions
                .map { ContextCompat.checkSelfPermission(context, it) }
                .any { result -> result == PackageManager.PERMISSION_GRANTED }

            if (granted) {
                loadWeather()
            } else {
                if (askedLocationPermission.not()) {
                    showAlertDialog = HomeDialogType.LoadWeatherPermission
                    askedLocationPermission = true
                }
            }
        }
    }

    when (showAlertDialog) {
        HomeDialogType.Dismiss -> {}

        HomeDialogType.LoadWeatherPermission -> LookaroundAlertDialog(
            text = stringResource(R.string.request_location_permission_message),
            onDismiss = {
                viewModel.updateWeatherStateLocationFail(
                    IllegalStateException("권한을 허용해주세요.")
                )
                onShowSnackBar("서비스를 이용하기 위해서 권한을 허용해주세요.")
                showAlertDialog = HomeDialogType.Dismiss
            },
            onConfirm = {
                val activity = FragmentComponentManager.findActivity(context) as Activity
                val shouldShowPermissionRationale = homeScreenPermissions
                    .map { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }
                    .any { it } // 하나 이상의 Permission이 근거 메세지를 보여줄 수 있다면

                // 앱 최초 설치 시 shouldShowPermissionRationale과 hasAskedLocationPermission이 false
                if (shouldShowPermissionRationale) {
                    permissionLauncher.launch(homeScreenPermissions)
                    viewModel.updateRequestedFineLocation()
                } else {
                    if (state.hasAskedLocationPermission) {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    } else {
                        permissionLauncher.launch(homeScreenPermissions)
                        viewModel.updateRequestedFineLocation()
                    }
                }
                showAlertDialog = HomeDialogType.Dismiss
            }
        )

        HomeDialogType.StartRecord -> LookaroundAlertDialog(
            text = stringResource(R.string.start_record_message),
            onDismiss = { showAlertDialog = HomeDialogType.Dismiss },
            onConfirm = {
                viewModel.startRecording()
                showAlertDialog = HomeDialogType.Dismiss
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

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                is Effect.Error -> {
                    onShowSnackBar(it.message)
                }


                Effect.StartRecordingService -> {
                    onStartRecordingService()
                    onRouteRecording()
                }
            }
        }
    }

    HomeScreen(
        state = state,
        onRouteRecording = {
            if (state.recording) {
                onRouteRecording()
            } else {
                recordPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        },
        onWeatherRetry = {
            if (homeScreenPermissions.checkPermission()) {
                loadWeather()
            } else {
                showAlertDialog = HomeDialogType.LoadWeatherPermission
            }
        },
        onClickFilter = {
            showSummaryFilterBottomSheet = true
        }
    )
}

@Composable
fun HomeScreen(
    state: UiState,
    onRouteRecording: () -> Unit,
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
            onStartWalking = onRouteRecording,
        )
        Spacer(modifier = Modifier.height(16.dp))
        WeatherCard(
            uiState = state.weatherUiState,
            onRetry = onWeatherRetry
        )
        Spacer(modifier = Modifier.height(16.dp))
        SummaryCard(
            state = state.summaryUiState,
            filter = when (state.summaryFilter) {
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
                hasAskedLocationPermission = true,
                summaryFilter = SummaryFilter.WEEK,
                weatherUiState = WeatherUiState.Result(
                    sky = Weather.Sky.SUNNY,
                    precipitation = "1.0mm",
                    windSpeed = "15m/s",
                    temperatures = "16도",
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
            onRouteRecording = {},
            onWeatherRetry = {},
            onClickFilter = {},
        )
    }
}

private enum class HomeDialogType {
    Dismiss,
    LoadWeatherPermission,
    StartRecord,
}