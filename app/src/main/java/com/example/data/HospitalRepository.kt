package com.example.data

import android.location.Location
import android.util.Log
import com.example.model.BedStatus
import com.example.model.CareLevel
import com.example.model.EmergencySpecialty
import com.example.model.Hospital
import com.example.model.LatLngPoint
import com.example.model.RouteInfo
import com.example.model.RouteStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HospitalRepository {
  private val TAG = "HospitalRepository"

  private val httpClient = OkHttpClient.Builder()
    .connectTimeout(8, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .build()

  /**
   * Returns a merged list of hospitals (pre-populated verified hospitals + OSM live query)
   * with calculated beeline distance and initial driving estimation.
   */
  suspend fun getNearbyHospitals(
    latitude: Double,
    longitude: Double,
    radiusMeters: Int = 35000
  ): List<Hospital> = withContext(Dispatchers.IO) {
    val resultList = mutableListOf<Hospital>()

    // 1. Add all pre-configured verified hospitals with distance calculated
    DefaultHospitals.list.forEach { defaultHosp ->
      val dist = FloatArray(1)
      Location.distanceBetween(
        latitude,
        longitude,
        defaultHosp.latitude,
        defaultHosp.longitude,
        dist
      )
      val beelineMeters = dist[0]
      // Estimated road distance (urban/suburban coefficient ~1.32)
      val estimatedDrivingDist = (beelineMeters * 1.32f).toInt()
      // Estimated RTW driving duration: ~42 km/h average with priority signal
      val estimatedDurationSec = ((estimatedDrivingDist / 1000.0 / 42.0) * 3600.0).toInt().coerceAtLeast(120)

      resultList.add(
        defaultHosp.copy(
          distanceMeters = beelineMeters,
          drivingDistanceMeters = estimatedDrivingDist,
          drivingDurationSeconds = estimatedDurationSec
        )
      )
    }

    // 2. Fetch live data from OpenStreetMap Overpass API (free, open, no key)
    try {
      val osmHospitals = fetchOverpassHospitals(latitude, longitude, radiusMeters)
      osmHospitals.forEach { osmHosp ->
        // Avoid duplicates if already present in default list by distance threshold (< 400m)
        val alreadyPresent = resultList.any { existing ->
          val distCheck = FloatArray(1)
          Location.distanceBetween(
            existing.latitude,
            existing.longitude,
            osmHosp.latitude,
            osmHosp.longitude,
            distCheck
          )
          distCheck[0] < 450f
        }
        if (!alreadyPresent) {
          resultList.add(osmHosp)
        }
      }
    } catch (e: Exception) {
      Log.w(TAG, "Overpass query failed or timed out, continuing with local dataset: ${e.message}")
    }

    // Sort ascending by beeline distance
    resultList.sortedBy { it.distanceMeters }
  }

  /**
   * Queries free OpenStreetMap Overpass API for emergency hospitals
   */
  private fun fetchOverpassHospitals(
    lat: Double,
    lon: Double,
    radiusMeters: Int
  ): List<Hospital> {
    val overpassQuery = """
      [out:json][timeout:8];
      (
        node["amenity"="hospital"](around:$radiusMeters,$lat,$lon);
        way["amenity"="hospital"](around:$radiusMeters,$lat,$lon);
        node["healthcare"="hospital"](around:$radiusMeters,$lat,$lon);
        way["healthcare"="hospital"](around:$radiusMeters,$lat,$lon);
      );
      out center tags 25;
    """.trimIndent()

    val encodedQuery = java.net.URLEncoder.encode(overpassQuery, "UTF-8")
    val url = "https://overpass-api.de/api/interpreter?data=$encodedQuery"

    val request = Request.Builder()
      .url(url)
      .header("User-Agent", "RtwNotaufnahmeNavigator/1.0 (Android Paramedic App)")
      .build()

    val response = httpClient.newCall(request).execute()
    if (!response.isSuccessful) return emptyList()

    val bodyString = response.body?.string() ?: return emptyList()
    val json = JSONObject(bodyString)
    val elements = json.optJSONArray("elements") ?: return emptyList()

    val list = mutableListOf<Hospital>()
    for (i in 0 until elements.length()) {
      val elem = elements.getJSONObject(i)
      val tags = elem.optJSONObject("tags") ?: continue

      val name = tags.optString("name").trim()
      if (name.isEmpty()) continue

      var elemLat = elem.optDouble("lat", Double.NaN)
      var elemLon = elem.optDouble("lon", Double.NaN)

      if (elemLat.isNaN() || elemLon.isNaN()) {
        val center = elem.optJSONObject("center")
        if (center != null) {
          elemLat = center.optDouble("lat", Double.NaN)
          elemLon = center.optDouble("lon", Double.NaN)
        }
      }
      if (elemLat.isNaN() || elemLon.isNaN()) continue

      val emergencyTag = tags.optString("emergency", "")
      val isEmergency = emergencyTag.equals("yes", ignoreCase = true) ||
          name.contains("Klinik", ignoreCase = true) ||
          name.contains("Krankenhaus", ignoreCase = true) ||
          name.contains("Hospital", ignoreCase = true) ||
          name.contains("Notaufnahme", ignoreCase = true)

      if (!isEmergency) continue

      val city = tags.optString("addr:city", tags.optString("city", ""))
      val street = tags.optString("addr:street", "")
      val houseNumber = tags.optString("addr:housenumber", "")
      val postalCode = tags.optString("addr:postcode", "")
      val fullAddress = if (street.isNotEmpty()) "$street $houseNumber".trim() else "Klinikgelände"
      val phone = tags.optString("phone", tags.optString("contact:phone", "+49 112"))

      // Detect specialties from tags
      val specialties = mutableSetOf(EmergencySpecialty.ZNA)
      val lowerName = name.lowercase()
      if (lowerName.contains("kinder") || lowerName.contains("pädiatr")) {
        specialties.add(EmergencySpecialty.PEDIATRIC)
      }
      if (lowerName.contains("unfall") || lowerName.contains("trauma") || lowerName.contains("bg klin")) {
        specialties.add(EmergencySpecialty.TRAUMA)
      }
      if (lowerName.contains("herz") || lowerName.contains("kardio")) {
        specialties.add(EmergencySpecialty.CPU)
      }
      if (lowerName.contains("neuro")) {
        specialties.add(EmergencySpecialty.NEURO)
        specialties.add(EmergencySpecialty.STROKE)
      }
      if (lowerName.contains("universit") || lowerName.contains("charité") || lowerName.contains("klinikum")) {
        specialties.add(EmergencySpecialty.CPU)
        specialties.add(EmergencySpecialty.STROKE)
        specialties.add(EmergencySpecialty.TRAUMA)
      }

      val careLevel = when {
        lowerName.contains("universit") || lowerName.contains("charité") -> CareLevel.MAXIMALVERSORGUNG
        lowerName.contains("klinikum") -> CareLevel.SCHWERPUNKT
        else -> CareLevel.REGELVERSORGUNG
      }

      val dist = FloatArray(1)
      Location.distanceBetween(lat, lon, elemLat, elemLon, dist)
      val beelineMeters = dist[0]
      val estimatedDrivingDist = (beelineMeters * 1.32f).toInt()
      val estimatedDurationSec = ((estimatedDrivingDist / 1000.0 / 42.0) * 3600.0).toInt().coerceAtLeast(120)

      list.add(
        Hospital(
          id = "osm_${elem.optString("id", i.toString())}",
          name = name,
          emergencyDepartmentName = "Notaufnahme / Notfallzentrum",
          latitude = elemLat,
          longitude = elemLon,
          address = fullAddress,
          city = city.ifEmpty { "Bereich Einsatzort" },
          postalCode = postalCode,
          phone = phone,
          emergencyPhone = phone,
          level = careLevel,
          specialties = specialties,
          status = BedStatus.AVAILABLE,
          statusNote = "OSM-verifiziertes Krankenhaus",
          hasHeliport = tags.optString("heliport", "").isNotEmpty(),
          distanceMeters = beelineMeters,
          drivingDistanceMeters = estimatedDrivingDist,
          drivingDurationSeconds = estimatedDurationSec
        )
      )
    }
    return list
  }

  /**
   * Fetches accurate driving route, turn-by-turn steps, and geometry using OSRM (free, open car routing API)
   * Falls back gracefully to geometric estimation if network is offline.
   */
  suspend fun calculateDrivingRoute(
    startLat: Double,
    startLon: Double,
    destLat: Double,
    destLon: Double,
    destName: String
  ): RouteInfo = withContext(Dispatchers.IO) {
    val startPoint = LatLngPoint(startLat, startLon)
    val destPoint = LatLngPoint(destLat, destLon)

    try {
      // OSRM coordinates: {longitude},{latitude}
      val url = "https://router.project-osrm.org/route/v1/driving/$startLon,$startLat;$destLon,$destLat?overview=full&geometries=geojson&steps=true"
      val request = Request.Builder()
        .url(url)
        .header("User-Agent", "RtwNotaufnahmeNavigator/1.0 (Android Paramedic App)")
        .build()

      val response = httpClient.newCall(request).execute()
      if (response.isSuccessful) {
        val body = response.body?.string()
        if (body != null) {
          val json = JSONObject(body)
          val routes = json.optJSONArray("routes")
          if (routes != null && routes.length() > 0) {
            val primaryRoute = routes.getJSONObject(0)
            val distanceMeters = primaryRoute.optDouble("distance", 0.0).toInt()
            val durationSeconds = primaryRoute.optDouble("duration", 0.0).toInt()

            // Coordinates for canvas map polyline
            val geometry = primaryRoute.optJSONObject("geometry")
            val coordsArray = geometry?.optJSONArray("coordinates")
            val polyline = mutableListOf<LatLngPoint>()
            if (coordsArray != null) {
              for (j in 0 until coordsArray.length()) {
                val pt = coordsArray.getJSONArray(j)
                polyline.add(LatLngPoint(latitude = pt.getDouble(1), longitude = pt.getDouble(0)))
              }
            }

            // Steps
            val steps = mutableListOf<RouteStep>()
            val legs = primaryRoute.optJSONArray("legs")
            if (legs != null && legs.length() > 0) {
              val firstLeg = legs.getJSONObject(0)
              val stepsArray = firstLeg.optJSONArray("steps")
              if (stepsArray != null) {
                for (s in 0 until stepsArray.length()) {
                  val stepObj = stepsArray.getJSONObject(s)
                  val stepDist = stepObj.optDouble("distance", 0.0)
                  val stepDur = stepObj.optDouble("duration", 0.0)
                  val maneuver = stepObj.optJSONObject("maneuver")
                  val maneuverType = maneuver?.optString("type", "") ?: ""
                  val modifier = maneuver?.optString("modifier", "") ?: ""
                  val streetName = stepObj.optString("name", "").ifEmpty { "Verbindungsstraße" }

                  val germanInstruction = translateManeuver(maneuverType, modifier, streetName)
                  steps.add(
                    RouteStep(
                      instruction = germanInstruction,
                      distanceMeters = stepDist,
                      durationSeconds = stepDur,
                      streetName = streetName
                    )
                  )
                }
              }
            }

            if (steps.isEmpty()) {
              steps.add(RouteStep("Dem Straßenverlauf zur Notaufnahme folgen", distanceMeters.toDouble(), durationSeconds.toDouble(), destName))
            }

            return@withContext RouteInfo(
              startPoint = startPoint,
              destinationPoint = destPoint,
              destinationName = destName,
              distanceMeters = distanceMeters,
              durationSeconds = durationSeconds,
              polylinePoints = if (polyline.isNotEmpty()) polyline else generateDirectLinePoints(startPoint, destPoint),
              steps = steps,
              isFallbackEstimate = false
            )
          }
        }
      }
    } catch (e: Exception) {
      Log.w(TAG, "OSRM online route calculation failed, using robust offline estimation: ${e.message}")
    }

    // Fallback: Geometric Route Model
    val dist = FloatArray(1)
    Location.distanceBetween(startLat, startLon, destLat, destLon, dist)
    val beeline = dist[0]
    val estimatedRoadDist = (beeline * 1.34f).toInt()
    val estimatedDur = ((estimatedRoadDist / 1000.0 / 42.0) * 3600.0).toInt().coerceAtLeast(120)

    val simulatedSteps = listOf(
      RouteStep("Vom RTW-Standort starten mit Sondersignal", estimatedRoadDist * 0.15, estimatedDur * 0.15, "Ortsstraße / Hauptverkehrsachse"),
      RouteStep("Hauptstraße / Zubringer Richtung $destName folgen", estimatedRoadDist * 0.70, estimatedDur * 0.70, "Einsatzroute"),
      RouteStep("Abbiegen zur Rettungsdienst-Zufahrt / Notaufnahme", estimatedRoadDist * 0.15, estimatedDur * 0.15, "Zufahrt Notaufnahme")
    )

    RouteInfo(
      startPoint = startPoint,
      destinationPoint = destPoint,
      destinationName = destName,
      distanceMeters = estimatedRoadDist,
      durationSeconds = estimatedDur,
      polylinePoints = generateDirectLinePoints(startPoint, destPoint),
      steps = simulatedSteps,
      isFallbackEstimate = true
    )
  }

  private fun translateManeuver(type: String, modifier: String, street: String): String {
    return when (type) {
      "depart" -> "Einsatzfahrt starten auf $street"
      "arrive" -> "Ziel erreicht: Notaufnahme $street"
      "turn" -> when (modifier) {
        "left" -> "Links abbiegen auf $street"
        "right" -> "Rechts abbiegen auf $street"
        "sharp left" -> "Scharf links abbiegen auf $street"
        "sharp right" -> "Scharf rechts abbiegen auf $street"
        "slight left" -> "Halb links abbiegen auf $street"
        "slight right" -> "Halb rechts abbiegen auf $street"
        else -> "Abbiegen auf $street"
      }
      "roundabout" -> "Im Kreisverkehr Ausfahrt Richtung $street nehmen"
      "merge" -> "Einfädeln auf $street"
      "fork" -> "An der Gabelung Ausfahrt Richtung $street wählen"
      "new name" -> "Weiter auf $street"
      else -> "Geradeaus weiter auf $street"
    }
  }

  private fun generateDirectLinePoints(p1: LatLngPoint, p2: LatLngPoint): List<LatLngPoint> {
    val count = 12
    val points = mutableListOf<LatLngPoint>()
    for (i in 0..count) {
      val frac = i / count.toDouble()
      val lat = p1.latitude + frac * (p2.latitude - p1.latitude)
      val lng = p1.longitude + frac * (p2.longitude - p1.longitude)
      points.add(LatLngPoint(lat, lng))
    }
    return points
  }
}
