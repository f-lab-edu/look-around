package kky.flab.lookaround.feature.home

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import dagger.hilt.android.AndroidEntryPoint
import kky.flab.lookaround.core.ui.util.millsToTimeFormat
import kky.flab.lookaround.feature.home.databinding.ActivityRecordingBinding
import kotlinx.coroutines.launch
import kky.flab.lookaround.core.ui.R as CoreUiResource

@AndroidEntryPoint
class RecordingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityRecordingBinding
    private lateinit var map: NaverMap

    private val viewModel: RecordingViewModel by viewModels()

    private val pathOverlay = PathOverlay()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapViewFm = supportFragmentManager.findFragmentById(R.id.map_view) as MapFragment?
            ?: MapFragment.newInstance(NaverMapOptions().zoomControlEnabled(false)).also {
                supportFragmentManager.beginTransaction().add(R.id.map_view, it).commitNow()
            }

        mapViewFm.getMapAsync(this)

        initView()
        observe()
        if (intent.getBooleanExtra("stop", false)) {
            showCompleteDialog()
        }
    }

    private fun initView() {
        binding.tvComplete.setOnClickListener {
            showCompleteDialog()
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect { record ->
                    if (record.path.size >= 2) {
                        pathOverlay.coords = record.path.map { LatLng(it.latitude, it.longitude) }
                        initPathOverlay()
                    }

                    binding.tvDistance.text = "${record.distance}m"
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.timer.collect {
                    val time = it.millsToTimeFormat()
                    binding.tvTime.text = time
                }
            }
        }
    }

    private fun showCompleteDialog() {
        AlertDialog.Builder(this).setMessage("산책을 종료할까요?").setPositiveButton("종료") { dialog, _ ->
            dialog.dismiss()
            viewModel.complete()
            finish()
        }.setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }.show()
    }

    private fun initPathOverlay() {
        if (pathOverlay.map == null && pathOverlay.coords.size >= 2 && this::map.isInitialized) {
            pathOverlay.map = map
            pathOverlay.width = 30
            pathOverlay.passedColor = getColor(CoreUiResource.color.primaryColor)
            pathOverlay.color = getColor(CoreUiResource.color.primaryColor)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(mapView: NaverMap) {
        this.map = mapView
        binding.zoom.map = mapView

        initPathOverlay()
        val locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationProviderClient.lastLocation.addOnSuccessListener {
            it?.let {
                val cameraUpdate =
                    CameraUpdate.scrollAndZoomTo(LatLng(it.latitude, it.longitude), 17.0)
                map.moveCamera(cameraUpdate)
            }
        }
    }
}
