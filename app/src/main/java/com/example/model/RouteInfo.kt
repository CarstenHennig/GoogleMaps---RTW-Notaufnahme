package com.example.model

data class LatLngPoint(
  val latitude: Double,
  val longitude: Double
)

data class RouteStep(
  val instruction: String,
  val distanceMeters: Double,
  val durationSeconds: Double,
  val streetName: String = ""
) {
  val formattedDistance: String
    get() = if (distanceMeters >= 1000) {
      String.format("%.1f km", distanceMeters / 1000)
    } else {
      "${distanceMeters.toInt()} m"
    }
}

data class RouteInfo(
  val startPoint: LatLngPoint,
  val destinationPoint: LatLngPoint,
  val destinationName: String,
  val distanceMeters: Int,
  val durationSeconds: Int,
  val polylinePoints: List<LatLngPoint>,
  val steps: List<RouteStep>,
  val isFallbackEstimate: Boolean = false
) {
  val distanceKmFormatted: String
    get() = String.format("%.1f km", distanceMeters / 1000.0)

  val durationMinutesFormatted: String
    get() = "${(durationSeconds / 60.0).toInt().coerceAtLeast(1)} Min"
}

data class RtwLocation(
  val latitude: Double,
  val longitude: Double,
  val accuracyMeters: Float = 5.0f,
  val speedKmH: Float = 0f,
  val altitudeMeters: Double = 0.0,
  val addressName: String = "",
  val isSimulated: Boolean = false,
  val timestamp: Long = System.currentTimeMillis()
)
