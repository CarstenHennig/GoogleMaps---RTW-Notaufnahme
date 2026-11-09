package com.example

import com.example.data.DefaultHospitals
import com.example.model.BedStatus
import com.example.model.CareLevel
import com.example.model.EmergencySpecialty
import com.example.model.Hospital
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun defaultHospitals_containsCertifiedEmergencyDepartments() {
    val hospitals = DefaultHospitals.list
    assertFalse(hospitals.isEmpty())
    val hasCpu = hospitals.any { it.specialties.contains(EmergencySpecialty.CPU) }
    val hasTrauma = hospitals.any { it.specialties.contains(EmergencySpecialty.TRAUMA) }
    val hasStroke = hospitals.any { it.specialties.contains(EmergencySpecialty.STROKE) }
    val hasPediatric = hospitals.any { it.specialties.contains(EmergencySpecialty.PEDIATRIC) }

    assertTrue("Hospitals list must contain CPU / Chest Pain Unit", hasCpu)
    assertTrue("Hospitals list must contain Trauma Center", hasTrauma)
    assertTrue("Hospitals list must contain Stroke Unit", hasStroke)
    assertTrue("Hospitals list must contain Pediatric Emergency", hasPediatric)
  }

  @Test
  fun hospitalModel_formatsDistancesAndDurations() {
    val testHospital = Hospital(
      id = "test_hospital",
      name = "Test Universitätsklinikum",
      emergencyDepartmentName = "ZNA",
      latitude = 52.5200,
      longitude = 13.4050,
      address = "Teststraße 1",
      city = "Berlin",
      postalCode = "10115",
      phone = "+49 30 12345",
      emergencyPhone = "+49 30 1234599",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(EmergencySpecialty.ZNA, EmergencySpecialty.CPU),
      status = BedStatus.AVAILABLE,
      distanceMeters = 3450f,
      drivingDistanceMeters = 4200,
      drivingDurationSeconds = 480
    )

    assertEquals("3.5 km", testHospital.formattedBeelineKm)
    assertEquals("4.2 km", testHospital.formattedDrivingDistanceKm)
    assertEquals("8 Min", testHospital.formattedDrivingDurationMinutes)
    assertNotNull(testHospital.emergencyPhone)
  }
}
