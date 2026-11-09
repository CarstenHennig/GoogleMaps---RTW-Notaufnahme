package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BedStatus
import com.example.model.EmergencySpecialty
import com.example.model.Hospital

@Composable
fun HospitalCard(
  hospital: Hospital,
  onStartNavigation: () -> Unit,
  onShowRoute: () -> Unit,
  onCallEmergency: () -> Unit,
  onOpenPreNotification: () -> Unit,
  onToggleStatus: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("hospital_card_${hospital.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Header: ETA & Driving Distance (Primary Paramedic Information)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // ETA Badge with Car/RTW
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.testTag("eta_badge_${hospital.id}")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.DirectionsCar,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = hospital.formattedDrivingDurationMinutes,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 17.sp,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = " (${hospital.formattedDrivingDistanceKm})",
              fontWeight = FontWeight.Medium,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
            )
          }
        }

        // Bed Status Pill (IVENA style)
        val statusColor = Color(hospital.status.colorHex)
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = statusColor.copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f)),
          modifier = Modifier
            .clickable(onClick = onToggleStatus)
            .testTag("status_pill_${hospital.id}")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(statusColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = hospital.status.label,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = statusColor
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Hospital Name & Emergency Department
      Text(
        text = hospital.name,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 21.sp
      )

      Text(
        text = "${hospital.emergencyDepartmentName} • ${hospital.level.shortName}",
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary
      )

      // Address & Beeline info
      Text(
        text = "${hospital.address}, ${hospital.postalCode} ${hospital.city} • ${hospital.formattedBeelineKm} Luftlinie",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      // Status Note if present
      if (hospital.statusNote.isNotEmpty()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Hinweis: ${hospital.statusNote}",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Specialty Badges Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        hospital.specialties.take(4).forEach { spec ->
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = spec.shortBadge,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }

        if (hospital.specialties.size > 4) {
          Text(
            text = "+${hospital.specialties.size - 4}",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        if (hospital.hasHeliport) {
          Spacer(modifier = Modifier.weight(1f))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Flight,
              contentDescription = "Hubschrauberlandeplatz vorhanden",
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Heliport (RTH)",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.secondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons (Optimized for RTW Gloves & Rapid Paramedic Operations)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Primary Action: "Navigation starten" (Direct turn-by-turn intent)
        Button(
          onClick = onStartNavigation,
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("nav_button_${hospital.id}"),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          ),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Directions,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Navigation",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }

        // Secondary Action: "Route anzeigen" (In-app canvas map & step list)
        OutlinedButton(
          onClick = onShowRoute,
          modifier = Modifier
            .height(48.dp)
            .testTag("route_button_${hospital.id}"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Map,
            contentDescription = "Route ansehen",
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Route", fontSize = 13.sp)
        }

        // Phone call to emergency desk
        FilledTonalButton(
          onClick = onCallEmergency,
          modifier = Modifier
            .size(48.dp)
            .testTag("call_button_${hospital.id}"),
          shape = RoundedCornerShape(10.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Notaufnahme anrufen",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }

        // Voranmeldung drafting
        FilledTonalButton(
          onClick = onOpenPreNotification,
          modifier = Modifier
            .size(48.dp)
            .testTag("prenotify_button_${hospital.id}"),
          shape = RoundedCornerShape(10.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
          Icon(
            imageVector = Icons.Default.NotificationAdd,
            contentDescription = "Voranmeldung erstellen",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}
