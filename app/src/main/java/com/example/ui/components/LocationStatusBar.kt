package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.location.CityPreset
import com.example.model.RtwLocation
import java.util.Locale

@Composable
fun LocationStatusBar(
  location: RtwLocation,
  isLiveGpsActive: Boolean,
  presets: List<CityPreset>,
  onToggleGps: () -> Unit,
  onSelectPreset: (CityPreset) -> Unit,
  onRefreshHospitals: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showPresetMenu by remember { mutableStateOf(false) }

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    tonalElevation = 2.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      // Row 1: Status badge & Quick Switchers
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Status indicator
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(
                if (isLiveGpsActive) Color(0xFF00E676) else Color(0xFFFFB300)
              )
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isLiveGpsActive) "LIVE GPS AKTIV" else "STANDORT: ${if (location.isSimulated) "SIMULIERT" else "GESPEICHERT"}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isLiveGpsActive) Color(0xFF00C853) else Color(0xFFE65100),
            letterSpacing = 0.5.sp
          )
        }

        // Actions
        Row(verticalAlignment = Alignment.CenterVertically) {
          // City preset dropdown button
          Box {
            FilledTonalButton(
              onClick = { showPresetMenu = true },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .height(34.dp)
                .testTag("preset_city_button")
            ) {
              Icon(
                imageVector = Icons.Default.LocationCity,
                contentDescription = "Stadt wechseln",
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Gebiet", fontSize = 12.sp)
            }

            DropdownMenu(
              expanded = showPresetMenu,
              onDismissRequest = { showPresetMenu = false }
            ) {
              Text(
                text = "Einsatzgebiet wählen:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
              presets.forEach { preset ->
                DropdownMenuItem(
                  text = {
                    Column {
                      Text(preset.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                      Text(preset.state, fontSize = 11.sp, color = Color.Gray)
                    }
                  },
                  onClick = {
                    showPresetMenu = false
                    onSelectPreset(preset)
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Live GPS Toggle
          IconButton(
            onClick = onToggleGps,
            modifier = Modifier
              .size(36.dp)
              .testTag("toggle_live_gps_button")
          ) {
            Icon(
              imageVector = if (isLiveGpsActive) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed,
              contentDescription = "GPS aktivieren/deaktivieren",
              tint = if (isLiveGpsActive) MaterialTheme.colorScheme.primary else Color.Gray
            )
          }

          // Refresh Hospitals
          IconButton(
            onClick = onRefreshHospitals,
            modifier = Modifier
              .size(36.dp)
              .testTag("refresh_hospitals_button")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Aktualisieren",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Row 2: Location Address
      Text(
        text = location.addressName.ifEmpty { "Standort wird ermittelt..." },
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1
      )

      Spacer(modifier = Modifier.height(4.dp))

      // Row 3: GPS Telemetry (Lat, Lon, Speed, Accuracy)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = String.format(Locale.US, "Koord: %.4f° N, %.4f° E (±%.0fm)", location.latitude, location.longitude, location.accuracyMeters),
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = if (location.speedKmH > 0) "${location.speedKmH.toInt()} km/h" else "Stand",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }
  }
}
