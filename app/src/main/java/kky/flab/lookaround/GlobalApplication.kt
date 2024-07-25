package kky.flab.lookaround

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import kky.flab.lookaround.feature.home.R

@HiltAndroidApp
class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        createFgsNotificationChannel()
    }

    private fun createFgsNotificationChannel() {
        val channelId = getString(R.string.foreground_service_notification_channel_id)
        val channelName = getString(R.string.foreground_service_notification_channel_name)

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setShowBadge(false)
        }

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}
