package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import com.example.model.RtwLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class CityPreset(
  val name: String,
  val state: String,
  val latitude: Double,
  val longitude: Double,
  val defaultStreet: String
)

class LocationTracker(private val context: Context) {
  private val TAG = "LocationTracker"

  val presets = listOf(
    CityPreset("Berlin (Mitte)", "Berlin", 52.5200, 13.4050, "Alexanderplatz"),
    CityPreset("München (Zentrum)", "Bayern", 48.1371, 11.5754, "Marienplatz"),
    CityPreset("Hamburg (Hafen)", "Hamburg", 53.5438, 9.9803, "Landungsbrücken"),
    CityPreset("Frankfurt am Main", "Hessen", 50.1071, 8.6638, "Hauptbahnhof"),
    CityPreset("Köln (Zentrum)", "NRW", 50.9364, 6.9528, "Neumarkt"),
    CityPreset("Stuttgart (Mitte)", "Baden-Württemberg", 48.7758, 9.1829, "Schlossplatz"),
    CityPreset("Nürnberg", "Bayern", 49.4478, 11.0805, "Hauptbahnhof"),
    CityPreset("Leipzig", "Sachsen", 51.3397, 12.3731, "Marktplatz"),
    CityPreset("Dortmund", "NRW", 51.5136, 7.4653, "Westfalenhalle")
  )

  private val fusedClient: FusedLocationProviderClient =
    LocationServices.getFusedLocationProviderClient(context)

  // Default initial location: Berlin Alexanderplatz
  private val _currentLocation = MutableStateFlow(
    RtwLocation(
      latitude = 52.5200,
      longitude = 13.4050,
      accuracyMeters = 8.0f,
      speedKmH = 0f,
      altitudeMeters = 34.0,
      addressName = "Berlin - Alexanderplatz (Einsatzgebiet Berlin-Mitte)",
      isSimulated = false
    )
  )
  val currentLocation: StateFlow<RtwLocation> = _currentLocation.asStateFlow()

  private val _isLiveGpsActive = MutableStateFlow(false)
  val isLiveGpsActive: StateFlow<Boolean> = _isLiveGpsActive.asStateFlow()

  private var locationCallback: LocationCallback? = null

  @SuppressLint("MissingPermission")
  fun startLiveGpsTracking(onPermissionRequired: () -> Unit) {
    stopLiveGpsTracking()

    val locationRequest = LocationRequest.Builder(
      Priority.PRIORITY_HIGH_ACCURACY,
      4000L // update interval in ms for RTW
    ).apply {
      setMinUpdateIntervalMillis(2000L)
      setMinUpdateDistanceMeters(5.0f)
      setWaitForAccurateLocation(false)
    }.build()

    locationCallback = object : LocationCallback() {
      override fun onLocationResult(result: LocationResult) {
        val last = result.lastLocation ?: return
        val speedKmh = if (last.hasSpeed()) (last.speed * 3.6f) else 0f

        updateLocationInternal(
          latitude = last.latitude,
          longitude = last.longitude,
          accuracy = last.accuracy,
          speedKmH = speedKmh,
          altitude = last.altitude,
          isSimulated = false
        )
      }
    }

    try {
      fusedClient.requestLocationUpdates(
        locationRequest,
        locationCallback!!,
        Looper.getMainLooper()
      ).addOnSuccessListener {
        _isLiveGpsActive.value = true
        Log.i(TAG, "Live GPS tracking started")
      }.addOnFailureListener { e ->
        Log.e(TAG, "Failed to start GPS updates: ${e.message}")
        _isLiveGpsActive.value = false
        onPermissionRequired()
      }

      // Also get last known location immediately
      fusedClient.lastLocation.addOnSuccessListener { last ->
        if (last != null) {
          updateLocationInternal(
            latitude = last.latitude,
            longitude = last.longitude,
            accuracy = last.accuracy,
            speedKmH = if (last.hasSpeed()) (last.speed * 3.6f) else 0f,
            altitude = last.altitude,
            isSimulated = false
          )
        }
      }
    } catch (e: SecurityException) {
      Log.w(TAG, "Location permission missing: ${e.message}")
      _isLiveGpsActive.value = false
      onPermissionRequired()
    }
  }

  fun stopLiveGpsTracking() {
    locationCallback?.let {
      fusedClient.removeLocationUpdates(it)
      locationCallback = null
    }
    _isLiveGpsActive.value = false
  }

  fun setSimulatedLocation(preset: CityPreset) {
    stopLiveGpsTracking()
    _currentLocation.value = RtwLocation(
      latitude = preset.latitude,
      longitude = preset.longitude,
      accuracyMeters = 3.0f,
      speedKmH = 48.0f, // RTW emergency transit speed
      altitudeMeters = 45.0,
      addressName = "${preset.name} - ${preset.defaultStreet}",
      isSimulated = true
    )
  }

  fun setCustomCoordinates(lat: Double, lng: Double, label: String = "Manuelle Koordinaten") {
    stopLiveGpsTracking()
    updateLocationInternal(
      latitude = lat,
      longitude = lng,
      accuracy = 5.0f,
      speedKmH = 0f,
      altitude = 0.0,
      isSimulated = true,
      customName = label
    )
  }

  private fun updateLocationInternal(
    latitude: Double,
    longitude: Double,
    accuracy: Float,
    speedKmH: Float,
    altitude: Double,
    isSimulated: Boolean,
    customName: String? = null
  ) {
    var resolvedName = customName ?: _currentLocation.value.addressName

    // Reverse geocode asynchronously
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val geocoder = Geocoder(context, Locale.GERMANY)
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
          val addr = addresses[0]
          val thoroughfare = addr.thoroughfare ?: ""
          val subThoroughfare = addr.subThoroughfare ?: ""
          val locality = addr.locality ?: ""
          val street = if (thoroughfare.isNotEmpty()) "$thoroughfare $subThoroughfare".trim() else ""
          resolvedName = when {
            street.isNotEmpty() && locality.isNotEmpty() -> "$locality, $street"
            locality.isNotEmpty() -> locality
            street.isNotEmpty() -> street
            else -> String.format(Locale.US, "%.4f, %.4f", latitude, longitude)
          }
        }
      } catch (e: Exception) {
        // Fallback to coordinates
        if (resolvedName.isEmpty() || resolvedName.startsWith("Berlin")) {
          resolvedName = String.format(Locale.US, "GPS: %.4f N, %.4f E", latitude, longitude)
        }
      }

      _currentLocation.value = RtwLocation(
        latitude = latitude,
        longitude = longitude,
        accuracyMeters = accuracy,
        speedKmH = speedKmH,
        altitudeMeters = altitude,
        addressName = resolvedName,
        isSimulated = isSimulated
      )
    }
  }
}
