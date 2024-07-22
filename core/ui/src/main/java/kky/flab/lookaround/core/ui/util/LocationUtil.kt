package kky.flab.lookaround.core.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.LocationServices
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("MissingPermission")
suspend fun getAddress(context: Context): Address? = suspendCoroutine { continuation ->
    LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener { location ->
        location?.let {
            val geocoder = Geocoder(context, Locale.KOREA)
            if (Build.VERSION_CODES.TIRAMISU > Build.VERSION.SDK_INT) {
                val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                continuation.resume(address?.first())
            } else {
                geocoder.getFromLocation(it.latitude, it.longitude, 1) { address ->
                    continuation.resume(address.first())
                }
            }
        }
    }
}