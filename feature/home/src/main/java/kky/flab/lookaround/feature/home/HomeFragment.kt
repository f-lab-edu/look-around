package kky.flab.lookaround.feature.home

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kky.flab.lookaround.core.domain.model.Weather
import kky.flab.lookaround.feature.home.databinding.FragmentHomeBinding
import kky.flab.lookaround.feature.home.model.Error
import kky.flab.lookaround.feature.home.model.ShowEndRecordingMessage
import kky.flab.lookaround.feature.home.model.ShowStartRecordingMessage
import kky.flab.lookaround.feature.home.model.WeatherUiState
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            viewModel.updateRequestedFinLocation()

        }

    private var isRequestedPermission: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentHomeBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        observe()
    }

    private fun initView() {
        binding.statusCard.setOnClickListener {
            viewModel.showRecordingMessage()
        }

        binding.btWeatherRetry.setOnClickListener {
            viewModel.loadWeather(requireContext())
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.setting.collect {
                    isRequestedPermission = it.requestFineLocation
                    checkPermission()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect {
                    handleRecordingState(it.recording)
                    handleWeatherState(it.weatherUiState)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.effect.collect {
                    when (it) {
                        is ShowEndRecordingMessage,
                        is ShowStartRecordingMessage,
                        -> {
                            showRecordingDialog(it.message) { _, _ ->
                                viewModel.toggleRecording()
                            }
                        }

                        is Error -> {
                            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun handleRecordingState(state: Boolean) {
        val message = if (state) R.string.status_on_message else R.string.status_off_message
        val subMessage =
            if (state) R.string.status_on_sub_message else R.string.status_off_sub_message

        binding.tvMessage.setText(message)
        binding.tvSubMessage.setText(subMessage)
    }

    private fun handleWeatherState(state: WeatherUiState) {
        binding.pbWeather.isVisible = state is WeatherUiState.Loading
        binding.weatherFailGroup.isVisible = state is WeatherUiState.Fail
        binding.weatherResultGroup.isVisible = state is WeatherUiState.Result

        when (state) {
            is WeatherUiState.Fail -> {
                binding.tvWeatherError.text = state.message
            }

            WeatherUiState.Loading -> Unit

            is WeatherUiState.Result -> {
                val data = state.data
                binding.imgSky.setImageResource(
                    when (data.sky) {
                        Weather.Sky.SUNNY -> R.drawable.sunny
                        Weather.Sky.CLOUDY -> R.drawable.cloudy
                        Weather.Sky.OVERCAST -> R.drawable.overcast
                        Weather.Sky.RAINY -> R.drawable.rainy
                        Weather.Sky.SNOW -> R.drawable.snow
                    }
                )

                binding.tvTemperaturesData.text = "${data.temperatures}도"
                binding.tvPrecipitationData.text = "${data.precipitation}mm"
                binding.tvWindSpeedData.text = "${data.windSpeed}m/s"
            }
        }
    }

    private fun showRecordingDialog(
        message: String,
        onPositiveButtonListener: (DialogInterface, Int) -> Unit,
    ) {
        val dialog = AlertDialog.Builder(requireActivity()).apply {
            setMessage(message)
            setPositiveButton("확인", onPositiveButtonListener)
            setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
        }

        dialog.show()
    }

    private fun showPermissionRationaleDialog(onPositiveButtonListener: (DialogInterface, Int) -> Unit) {
        val dialog = AlertDialog.Builder(requireActivity()).apply {
            setMessage("현재 날씨 정보를 가져오기 위해서 위치 권한이 필요합니다.")
            setPositiveButton("확인", onPositiveButtonListener)
            setCancelable(false)
        }

        dialog.show()
    }

    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        var shouldShowRationale = true

        for (permission in permissions) {
            if (shouldShowRequestPermissionRationale(permission).not()) {
                shouldShowRationale = false
                break
            }
        }

        if (shouldShowRationale) {
            showPermissionRationaleDialog { _, _ ->
                requestPermission(permissions)
            }
        } else {
            var granted = false
            for (permission in permissions) {
                granted = ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }

            if (granted) {
                viewModel.loadWeather(requireContext())
            } else {
                showPermissionRationaleDialog { _, _ ->
                    if (isRequestedPermission) {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${requireActivity().packageName}")
                        )
                        startActivity(intent)
                    } else {
                        requestPermission(permissions)
                    }
                }
            }
        }
    }

    private fun requestPermission(permissions: Array<String>) {
        permissionLauncher.launch(permissions)
    }
}