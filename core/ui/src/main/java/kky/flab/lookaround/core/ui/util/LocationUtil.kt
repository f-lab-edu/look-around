package kky.flab.lookaround.core.ui.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.LocationServices
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@SuppressLint("MissingPermission")
suspend fun getAddress(context: Context): Address? = suspendCoroutine { continuation ->
    val locationServices = LocationServices.getFusedLocationProviderClient(context)
    locationServices.lastLocation.addOnSuccessListener { location ->
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
        } ?: continuation.resumeWithException(
            IllegalStateException("주소를 가져올 수 없습니다.\n위치 서비스 또는 인터넷 연결을 확인해주세요.")
        )
    }.addOnFailureListener { e ->
        continuation.resumeWithException(Exception("주소를 가져오는 데 실패하였습니다.\n재시도 해주세요.",e))
    }
}