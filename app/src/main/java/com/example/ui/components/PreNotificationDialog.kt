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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Hospital
import com.example.viewmodel.PreNotificationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreNotificationDialog(
  hospital: Hospital?,
  state: PreNotificationState,
  onUpdate: ((PreNotificationState) -> PreNotificationState) -> Unit,
  onDismiss: () -> Unit,
  onShare: () -> Unit,
  onCall: () -> Unit
) {
  val targetName = hospital?.name ?: "Zentrale Notaufnahme"

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("prenotification_dialog")
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "RTW VORANMELDUNG (SBAR)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              letterSpacing = 0.5.sp
            )
            Text(
              text = targetName,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Schließen")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Triage Selector (ROT, GELB, GRÜN)
        Text("Triage-Kategorie:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          TriageButton(
            label = "ROT (Schockraum)",
            color = Color(0xFFD32F2F),
            isSelected = state.triageColor == "ROT",
            onClick = { onUpdate { it.copy(triageColor = "ROT") } },
            modifier = Modifier.weight(1f)
          )
          TriageButton(
            label = "GELB (Dringend)",
            color = Color(0xFFFFA000),
            isSelected = state.triageColor == "GELB",
            onClick = { onUpdate { it.copy(triageColor = "GELB") } },
            modifier = Modifier.weight(1f)
          )
          TriageButton(
            label = "GRÜN (Stabil)",
            color = Color(0xFF2E7D32),
            isSelected = state.triageColor == "GRÜN",
            onClick = { onUpdate { it.copy(triageColor = "GRÜN") } },
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Verdachtsdiagnose
        OutlinedTextField(
          value = state.mainSymptom,
          onValueChange = { newText -> onUpdate { it.copy(mainSymptom = newText) } },
          label = { Text("Verdachtsdiagnose / Leitsymptom") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Age, Gender, ETA
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = state.patientAge,
            onValueChange = { age -> onUpdate { it.copy(patientAge = age) } },
            label = { Text("Alter") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = state.patientGender,
            onValueChange = { gender -> onUpdate { it.copy(patientGender = gender) } },
            label = { Text("Geschlecht") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = state.etaMinutes,
            onValueChange = { eta -> onUpdate { it.copy(etaMinutes = eta) } },
            label = { Text("ETA (Min)") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Vitals
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = state.vitalsHeartRate,
            onValueChange = { hf -> onUpdate { it.copy(vitalsHeartRate = hf) } },
            label = { Text("HF") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = state.vitalsBloodPressure,
            onValueChange = { rr -> onUpdate { it.copy(vitalsBloodPressure = rr) } },
            label = { Text("RR") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = state.vitalsSpO2,
            onValueChange = { sp -> onUpdate { it.copy(vitalsSpO2 = sp) } },
            label = { Text("SpO2") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Besonderheiten
        OutlinedTextField(
          value = state.specialInfo,
          onValueChange = { spec -> onUpdate { it.copy(specialInfo = spec) } },
          label = { Text("Besonderheiten / Maßnahmen") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 2
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Action Buttons: Share / Call
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = onShare,
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("share_prenotification_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Voranmeldung teilen")
          }

          FilledTonalButton(
            onClick = onCall,
            modifier = Modifier
              .height(48.dp)
              .testTag("call_from_prenotification_button"),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Anrufen")
          }
        }
      }
    }
  }
}

@Composable
private fun TriageButton(
  label: String,
  color: Color,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isSelected) color else color.copy(alpha = 0.12f),
    modifier = modifier
      .clickable(onClick = onClick)
      .height(38.dp)
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) Color.White else color
      )
    }
  }
}
