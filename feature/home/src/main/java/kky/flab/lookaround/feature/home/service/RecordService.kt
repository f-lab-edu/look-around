package kky.flab.lookaround.feature.home.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kky.flab.lookaround.core.domain.RecordRepository
import kky.flab.lookaround.core.domain.model.Path
import kky.flab.lookaround.feature.home.R
import kky.flab.lookaround.feature.recording.RecordingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecordService : LifecycleService() {

    @Inject
    lateinit var recordRepository: RecordRepository

    private lateinit var locationProviderClient: FusedLocationProviderClient

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            if (result.locations.isNotEmpty()) {
                result.locations.forEach { location ->
                    location?.let {
                        recordRepository.addPath(
                            Path(
                                latitude = it.latitude,
                                longitude = it.longitude
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        lifecycleScope.launch {
            recordRepository.recording.collect { recording ->
                if (recording.not()) {
                    stopSelf()
                }
            }
        }

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
        showNotification()

        return START_STICKY
    }

    override fun onDestroy() {
        locationProviderClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(3000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setIntervalMillis(5000)
            .build()

        locationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnSuccessListener {
            Log.d("RecordService", "트래킹 시작")
        }.addOnFailureListener {
            Log.e("RecordService", "트래킹 시도 실패 ${it.message}")
            CoroutineScope(Dispatchers.IO).launch {
                recordRepository.endRecording()
            }
            stopSelf()
        }
    }

    private fun showNotification() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelId = getString(R.string.foreground_service_notification_channel_id)

        val notificationClickIntent = Intent(
            this,
            RecordingActivity::class.java
        ).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        val stopIntent = Intent(this, RecordingActivity::class.java).apply {
            putExtra("stop", true)
            setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("산책을 기록중입니다.")
            .setContentText("산책을 끝내고 싶다면 아래 '종료' 버튼을 눌러주세요.")
            .setSmallIcon(R.drawable.sunny)
            .setContentIntent(getPendingIntent(1, notificationClickIntent))
            .addAction(R.drawable.baseline_stop_24, "산책 종료", getPendingIntent(2, stopIntent))
            .setOngoing(true)
            .build()

        manager.notify(1, notification)

        if (Build.VERSION_CODES.Q > Build.VERSION.SDK_INT) {
            startForeground(1, notification)
        } else {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        }
    }

    private fun getPendingIntent(requestCode: Int, intent: Intent): PendingIntent {
        return PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
