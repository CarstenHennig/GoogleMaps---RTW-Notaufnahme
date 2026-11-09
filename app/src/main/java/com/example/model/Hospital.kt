package com.example.model

enum class CareLevel(val displayName: String, val shortName: String) {
  MAXIMALVERSORGUNG("Maximalversorgung / Universitätsklinikum", "Maximal"),
  SCHWERPUNKT("Schwerpunktversorgung", "Schwerpunkt"),
  REGELVERSORGUNG("Regelversorgung", "Regel"),
  GRUNDVERSORGUNG("Grundversorgung", "Grund"),
  FACHKLINIK("Spezialisierte Fachklinik", "Fach")
}

enum class EmergencySpecialty(
  val id: String,
  val label: String,
  val shortBadge: String,
  val description: String
) {
  ZNA("zna", "ZNA (Notaufnahme)", "ZNA", "Zentrale Notaufnahme 24/7"),
  CPU("cpu", "Chest Pain Unit", "CPU", "Herzkatheterlabor 24/7 (STEMI)"),
  STROKE("stroke", "Stroke Unit", "Stroke", "Schlaganfallversorgung & Lyse/Thrombektomie"),
  TRAUMA("trauma", "Traumazentrum", "Trauma", "Schockraum & Polytraumaversorgung"),
  PEDIATRIC("pediatric", "Kinder-Notaufnahme", "Pädiatrie", "Notfallversorgung für Säuglinge & Kinder"),
  BURN("burn", "Verbrennungszentrum", "Verbrennung", "Schwerbrandverletzte"),
  PERINATAL("perinatal", "Perinatalzentrum (L1)", "Perinatal", "Notfallgynäkologie & Frühgeborene"),
  NEURO("neuro", "Neurochirurgie", "Neuro", "Notfall-Neurochirurgie & Schädel-Hirn-Trauma"),
  PSYCH("psych", "Krisen-/Notfallpsychiatrie", "Psych", "Akut- und Notfallpsychiatrie")
}

enum class BedStatus(val label: String, val colorHex: Long) {
  AVAILABLE("Aufnahmebereit", 0xFF2E7D32),
  RESTRICTED("Eingeschränkt", 0xFFF57F17),
  CLOSED("Vorübergehend abgemeldet", 0xFFC62828)
}

data class Hospital(
  val id: String,
  val name: String,
  val emergencyDepartmentName: String = "Zentrale Notaufnahme (ZNA)",
  val latitude: Double,
  val longitude: Double,
  val address: String,
  val city: String,
  val postalCode: String,
  val phone: String,
  val emergencyPhone: String,
  val level: CareLevel,
  val specialties: Set<EmergencySpecialty>,
  val status: BedStatus = BedStatus.AVAILABLE,
  val statusNote: String = "",
  val hasHeliport: Boolean = false,
  val distanceMeters: Float = 0f,
  val drivingDistanceMeters: Int? = null,
  val drivingDurationSeconds: Int? = null
) {
  val formattedBeelineKm: String
    get() {
      val km = distanceMeters / 1000f
      return if (km < 10) String.format("%.1f km", km) else String.format("%.0f km", km)
    }

  val formattedDrivingDistanceKm: String
    get() {
      val meters = drivingDistanceMeters ?: (distanceMeters * 1.32f).toInt()
      val km = meters / 1000.0
      return if (km < 10) String.format("%.1f km", km) else String.format("%.0f km", km)
    }

  val formattedDrivingDurationMinutes: String
    get() {
      val seconds = drivingDurationSeconds ?: run {
        // Fallback approximation: 40 km/h avg emergency transit speed
        val distKm = (drivingDistanceMeters ?: (distanceMeters * 1.32f).toInt()) / 1000.0
        ((distKm / 42.0) * 3600.0).toInt().coerceAtLeast(120)
      }
      val minutes = (seconds / 60.0).toInt().coerceAtLeast(1)
      return "$minutes Min"
    }
}
