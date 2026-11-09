package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HospitalRepository
import com.example.location.CityPreset
import com.example.location.LocationTracker
import com.example.model.BedStatus
import com.example.model.EmergencySpecialty
import com.example.model.Hospital
import com.example.model.RouteInfo
import com.example.model.RtwLocation
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class PreNotificationState(
  val triageColor: String = "ROT", // ROT, GELB, GRÜN
  val patientAge: String = "55",
  val patientGender: String = "männlich",
  val mainSymptom: String = "Akutes Koronarsyndrom (STEMI)",
  val vitalsHeartRate: String = "110",
  val vitalsBloodPressure: String = "90/60",
  val vitalsSpO2: String = "94%",
  val vitalsGcs: String = "15",
  val etaMinutes: String = "12",
  val specialInfo: String = "Intubation nicht erforderlich, RTW Voranmeldung via Leitstelle"
)

enum class SortMode(val label: String) {
  DRIVING_TIME("Fahrzeit (PKW/RTW)"),
  DISTANCE("Luftlinie")
}

class RtwViewModel(application: Application) : AndroidViewModel(application) {
  private val TAG = "RtwViewModel"

  val locationTracker = LocationTracker(application.applicationContext)
  private val repository = HospitalRepository()

  val currentLocation: StateFlow<RtwLocation> = locationTracker.currentLocation
  val isLiveGpsActive: StateFlow<Boolean> = locationTracker.isLiveGpsActive

  private val _hospitals = MutableStateFlow<List<Hospital>>(emptyList())
  val hospitals: StateFlow<List<Hospital>> = _hospitals.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedSpecialty = MutableStateFlow<EmergencySpecialty?>(null)
  val selectedSpecialty: StateFlow<EmergencySpecialty?> = _selectedSpecialty.asStateFlow()

  private val _sortMode = MutableStateFlow(SortMode.DRIVING_TIME)
  val sortMode: StateFlow<SortMode> = _sortMode.asStateFlow()

  // Selected hospital for route preview and step-by-step navigation sheet
  private val _selectedHospitalForRoute = MutableStateFlow<Hospital?>(null)
  val selectedHospitalForRoute: StateFlow<Hospital?> = _selectedHospitalForRoute.asStateFlow()

  private val _currentRouteInfo = MutableStateFlow<RouteInfo?>(null)
  val currentRouteInfo: StateFlow<RouteInfo?> = _currentRouteInfo.asStateFlow()

  private val _isCalculatingRoute = MutableStateFlow(false)
  val isCalculatingRoute: StateFlow<Boolean> = _isCalculatingRoute.asStateFlow()

  // Pre-notification state
  private val _preNotification = MutableStateFlow(PreNotificationState())
  val preNotification: StateFlow<PreNotificationState> = _preNotification.asStateFlow()

  private val _showPreNotificationDialog = MutableStateFlow(false)
  val showPreNotificationDialog: StateFlow<Boolean> = _showPreNotificationDialog.asStateFlow()

  private var fetchJob: Job? = null

  init {
    viewModelScope.launch {
      currentLocation.collectLatest { loc ->
        refreshHospitalsForLocation(loc.latitude, loc.longitude)
      }
    }
  }

  fun onSearchQueryChanged(query: String) {
    _searchQuery.value = query
  }

  fun onSpecialtyFilterSelected(specialty: EmergencySpecialty?) {
    _selectedSpecialty.value = if (_selectedSpecialty.value == specialty) null else specialty
  }

  fun onSortModeChanged(mode: SortMode) {
    _sortMode.value = mode
  }

  fun startLiveGps(onPermissionRequired: () -> Unit) {
    locationTracker.startLiveGpsTracking(onPermissionRequired)
  }

  fun stopLiveGps() {
    locationTracker.stopLiveGpsTracking()
  }

  fun selectPreset(preset: CityPreset) {
    locationTracker.setSimulatedLocation(preset)
  }

  fun setCustomCoordinates(lat: Double, lng: Double, label: String) {
    locationTracker.setCustomCoordinates(lat, lng, label)
  }

  fun refreshHospitals() {
    val loc = currentLocation.value
    refreshHospitalsForLocation(loc.latitude, loc.longitude)
  }

  private fun refreshHospitalsForLocation(lat: Double, lon: Double) {
    fetchJob?.cancel()
    fetchJob = viewModelScope.launch {
      _isLoading.value = true
      try {
        val list = repository.getNearbyHospitals(lat, lon)
        _hospitals.value = list

        // If a hospital was selected for route preview, update its route
        val currentSelected = _selectedHospitalForRoute.value
        if (currentSelected != null) {
          selectHospitalForRoute(currentSelected)
        }
      } catch (e: Exception) {
        Log.e(TAG, "Error refreshing hospitals: ${e.message}")
      } finally {
        _isLoading.value = false
      }
    }
  }

  fun selectHospitalForRoute(hospital: Hospital) {
    _selectedHospitalForRoute.value = hospital
    val loc = currentLocation.value
    viewModelScope.launch {
      _isCalculatingRoute.value = true
      try {
        val route = repository.calculateDrivingRoute(
          startLat = loc.latitude,
          startLon = loc.longitude,
          destLat = hospital.latitude,
          destLon = hospital.longitude,
          destName = hospital.name
        )
        _currentRouteInfo.value = route

        // Update ETA in pre-notification
        _preNotification.value = _preNotification.value.copy(
          etaMinutes = route.durationMinutesFormatted.replace(" Min", "").trim()
        )
      } catch (e: Exception) {
        Log.e(TAG, "Error calculating route: ${e.message}")
      } finally {
        _isCalculatingRoute.value = false
      }
    }
  }

  fun clearSelectedRoute() {
    _selectedHospitalForRoute.value = null
    _currentRouteInfo.value = null
  }

  /**
   * 1-Tap navigation launcher into Google Maps / Android Navigation app
   */
  fun launchTurnByTurnNavigation(hospital: Hospital) {
    val context = getApplication<Application>().applicationContext
    try {
      // Emergency turn-by-turn navigation intent
      val gmmIntentUri = Uri.parse("google.navigation:q=${hospital.latitude},${hospital.longitude}&mode=d")
      val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        setPackage("com.google.android.apps.maps")
      }
      if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
      } else {
        // Fallback to generic geo intent for any installed navigation app (OsmAnd, Waze, etc.)
        val geoUri = Uri.parse("geo:${hospital.latitude},${hospital.longitude}?q=${hospital.latitude},${hospital.longitude}(${Uri.encode(hospital.name)})")
        val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(fallbackIntent)
      }
    } catch (e: Exception) {
      Log.e(TAG, "Could not launch navigation: ${e.message}")
      // Ultimate browser fallback: OpenStreetMap routing
      val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${hospital.latitude},${hospital.longitude}&travelmode=driving")
      val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(webIntent)
    }
  }

  /**
   * 1-Tap direct phone call intent to hospital emergency desk
   */
  fun callHospitalEmergency(hospital: Hospital) {
    val context = getApplication<Application>().applicationContext
    try {
      val cleanNumber = hospital.emergencyPhone.replace(" ", "").replace("-", "")
      val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber")).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(dialIntent)
    } catch (e: Exception) {
      Log.e(TAG, "Could not open dialer: ${e.message}")
    }
  }

  fun openPreNotificationDialog(hospital: Hospital? = null) {
    if (hospital != null) {
      _selectedHospitalForRoute.value = hospital
    }
    _showPreNotificationDialog.value = true
  }

  fun closePreNotificationDialog() {
    _showPreNotificationDialog.value = false
  }

  fun updatePreNotification(update: (PreNotificationState) -> PreNotificationState) {
    _preNotification.value = update(_preNotification.value)
  }

  fun generateVoranmeldungText(hospital: Hospital?): String {
    val hName = hospital?.name ?: "Zentrale Notaufnahme"
    val p = _preNotification.value
    return """
      🚨 RTW VORANMELDUNG 🚨
      Ziel: $hName
      Triage: [${p.triageColor}]
      Patient: ${p.patientAge} J., ${p.patientGender}
      Verdachtsdiagnose: ${p.mainSymptom}
      Vitalparameter: HF ${p.vitalsHeartRate}/min | RR ${p.vitalsBloodPressure} mmHg | SpO2 ${p.vitalsSpO2} | GCS ${p.vitalsGcs}
      ETA (Ankunftszeit): ca. ${p.etaMinutes} Minuten
      Besonderheiten: ${p.specialInfo}
      Aktueller RTW Standort: ${currentLocation.value.addressName}
    """.trimIndent()
  }

  fun shareVoranmeldung(hospital: Hospital?) {
    val text = generateVoranmeldungText(hospital)
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, text)
      type = "text/plain"
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    val shareIntent = Intent.createChooser(sendIntent, "RTW Voranmeldung übermitteln").apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    getApplication<Application>().startActivity(shareIntent)
  }

  fun toggleHospitalBedStatus(hospitalId: String) {
    _hospitals.value = _hospitals.value.map { hosp ->
      if (hosp.id == hospitalId) {
        val nextStatus = when (hosp.status) {
          BedStatus.AVAILABLE -> BedStatus.RESTRICTED
          BedStatus.RESTRICTED -> BedStatus.CLOSED
          BedStatus.CLOSED -> BedStatus.AVAILABLE
        }
        val note = when (nextStatus) {
          BedStatus.AVAILABLE -> "Aufnahmebereit"
          BedStatus.RESTRICTED -> "Hohe Auslastung (Schockraum frei)"
          BedStatus.CLOSED -> "Temporär abgemeldet (Kapazitätsgrenze)"
        }
        hosp.copy(status = nextStatus, statusNote = note)
      } else {
        hosp
      }
    }
  }

  /**
   * Filtered and sorted hospital list
   */
  fun getFilteredHospitals(): List<Hospital> {
    val query = _searchQuery.value.trim().lowercase()
    val filterSpec = _selectedSpecialty.value
    val mode = _sortMode.value

    return _hospitals.value.filter { hosp ->
      val matchesQuery = query.isEmpty() ||
          hosp.name.lowercase().contains(query) ||
          hosp.city.lowercase().contains(query) ||
          hosp.address.lowercase().contains(query)

      val matchesSpecialty = filterSpec == null || hosp.specialties.contains(filterSpec)

      matchesQuery && matchesSpecialty
    }.let { list ->
      when (mode) {
        SortMode.DRIVING_TIME -> list.sortedBy { it.drivingDurationSeconds ?: (it.distanceMeters * 1.32f).toInt() }
        SortMode.DISTANCE -> list.sortedBy { it.distanceMeters }
      }
    }
  }
}
