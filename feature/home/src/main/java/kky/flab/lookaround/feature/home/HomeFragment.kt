package kky.flab.lookaround.feature.home

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kky.flab.lookaround.core.domain.const.SummaryFilter
import kky.flab.lookaround.core.domain.model.Weather
import kky.flab.lookaround.core.ui.util.getAddress
import kky.flab.lookaround.core.ui.util.getThemeColor
import kky.flab.lookaround.core.ui.util.xlsx.XlsxParser
import kky.flab.lookaround.feature.home.databinding.FragmentHomeBinding
import kky.flab.lookaround.feature.home.model.Effect
import kky.flab.lookaround.feature.home.model.SummaryUiState
import kky.flab.lookaround.feature.home.model.WeatherUiState
import kky.flab.lookaround.feature.home.service.RecordService
import kky.flab.lookaround.feature.recording.RecordingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.containsKey(Manifest.permission.ACCESS_FINE_LOCATION)
                || result.containsKey(Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                if (isRequestedPermission.not()) {
                    viewModel.updateRequestedFinLocation()
                }
                loadWeather()
            } else if (result.containsKey(Manifest.permission.POST_NOTIFICATIONS)) {
                requireContext().startForegroundService(
                    Intent(
                        requireActivity(),
                        RecordService::class.java
                    )
                )
            }
        }

    private var isRequestedPermission: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentHomeBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        observe()
    }

    private fun initView() {
        binding.spFilter.adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner_selected,
            SummaryFilter.entries.toTypedArray().map {
                when(it) {
                    SummaryFilter.WEEK -> "일주일"
                    SummaryFilter.MONTH -> "한달"
                    SummaryFilter.YEAR -> "1년"
                }
            }
        ).apply {
            setDropDownViewResource(R.layout.item_drop_down)
        }

        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.changeSummaryFilter(SummaryFilter.entries[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //none
            }

        }

        binding.statusCard.setOnClickListener {
            startRecording()
        }

        binding.btWeatherRetry.setOnClickListener {
            loadWeather()
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.config.collect {
                    isRequestedPermission = it.requestFineLocation
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect {
                    handleRecordingState(it.recording)
                    handleWeatherState(it.weatherUiState)
                    handleSummaryState(it.summaryUiState)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.effect.collect {
                    when (it) {
                        is Effect.Error -> Toast.makeText(
                            requireActivity(),
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        is Effect.ShowEndRecordingMessage -> {
                            requireContext().startActivity(
                                Intent(requireContext(), RecordingActivity::class.java)
                            )
                        }

                        is Effect.ShowStartRecordingMessage -> {
                            showRecordingDialog("산책을 시작해볼까요?") { _, _ ->
                                viewModel.startRecording()
                            }
                        }

                        Effect.StartRecordingService -> {
                            requireContext().startForegroundService(
                                Intent(
                                    requireActivity(),
                                    RecordService::class.java
                                )
                            )

                            startActivity(Intent(requireContext(), RecordingActivity::class.java))
                        }
                    }
                }
            }
        }
    }

    private fun handleRecordingState(state: Boolean) {
        val message = if (state) R.string.status_on_message else R.string.status_off_message
        val subMessage = if (state) R.string.status_on_sub_message else R.string.status_off_sub_message

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

    private fun handleSummaryState(state: SummaryUiState) {
        binding.pbSummary.isVisible = state is SummaryUiState.Loading
        binding.llSummary.isVisible = state is SummaryUiState.Result
        binding.tvSummaryEmpty.isVisible = state is SummaryUiState.Empty

        when(state) {
            SummaryUiState.Loading -> { /* none */}
            SummaryUiState.Empty -> { /* none */}
            is SummaryUiState.Result -> {
                val count = state.summary.count.toString() + "회"
                val time = StringBuilder().apply {
                    val data = state.summary.time
                    val hour = TimeUnit.MILLISECONDS.toHours(data)
                    if (hour > 0) {
                        append("${hour}시간")
                    }
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(data) % 60
                    if (minutes > 0) {
                        append("${minutes}분")
                    }
                }

                val mostOfWeek = state.summary.mostDayOfWeek + "요일"

                binding.tvSummaryCount.text = getSpannable("이 기간 동안 산책 $count", count)
                binding.tvSummaryTime.text = getSpannable("총 산책한 시간은 $time", time.toString())
                binding.tvSummaryBestDayOfWeek.text = getSpannable("산책을 가장 자주 한 날은 $mostOfWeek", mostOfWeek)
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
            setMessage("서비스를 이용하기 위해 자세한 위치 권한을 허용해주세요.")
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
                loadWeather()
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

    private fun startRecording() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            showPermissionRationaleDialog { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${requireContext().packageName}")
                )
                startActivity(intent)
            }
            return
        }

        val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER).not()) {
            val dialog = AlertDialog.Builder(requireContext()).apply {
                setMessage("서비스를 이용하기 위해 GPS를 켜주세요.")
                setPositiveButton("확인") { dialog, _ ->
                    dialog.dismiss()
                }
                setCancelable(false)
            }

            dialog.show()
            return
        }

        viewModel.showRecordingMessage()
    }

    private fun loadWeather() {
        lifecycleScope.launch {
            val context = requireContext()
            val address = getAddress(context) ?: return@launch
            val parseResult = withContext(Dispatchers.IO) {
                XlsxParser.findXY(context, address)
            }

            viewModel.loadWeather(parseResult.nx, parseResult.ny)
        }
    }

    private fun getSpannable(arg: String, target: String): Spannable {
        val builder = SpannableStringBuilder(arg)
        val targetIndex = arg.indexOf(target)
        builder.setSpan(
            StyleSpan(Typeface.BOLD),
            targetIndex,
            targetIndex + target.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            ForegroundColorSpan(requireContext().getThemeColor(androidx.appcompat.R.attr.colorPrimary)),
            targetIndex,
            targetIndex + target.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return builder
    }
}
