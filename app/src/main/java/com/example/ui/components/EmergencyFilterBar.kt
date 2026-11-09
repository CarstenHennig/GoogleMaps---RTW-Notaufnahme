package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmergencySpecialty

@Composable
fun EmergencyFilterBar(
  selectedSpecialty: EmergencySpecialty?,
  onSelectSpecialty: (EmergencySpecialty?) -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Row(
    modifier = modifier
      .fillMaxWidth()
      .horizontalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // "Alle Notaufnahmen" Chip
    FilterChip(
      selected = selectedSpecialty == null,
      onClick = { onSelectSpecialty(null) },
      label = { Text("Alle Notaufnahmen", fontSize = 12.sp) },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.LocalHospital,
          contentDescription = null,
          modifier = Modifier.padding(end = 4.dp)
        )
      },
      colors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
      ),
      modifier = Modifier.testTag("filter_chip_all")
    )

    // CPU (Herzinfarkt / STEMI)
    SpecialtyChip(
      specialty = EmergencySpecialty.CPU,
      icon = Icons.Default.Favorite,
      isSelected = selectedSpecialty == EmergencySpecialty.CPU,
      onSelect = { onSelectSpecialty(EmergencySpecialty.CPU) }
    )

    // Stroke Unit (Schlaganfall)
    SpecialtyChip(
      specialty = EmergencySpecialty.STROKE,
      icon = Icons.Default.Psychology,
      isSelected = selectedSpecialty == EmergencySpecialty.STROKE,
      onSelect = { onSelectSpecialty(EmergencySpecialty.STROKE) }
    )

    // Traumazentrum (Schockraum)
    SpecialtyChip(
      specialty = EmergencySpecialty.TRAUMA,
      icon = Icons.Default.MedicalServices,
      isSelected = selectedSpecialty == EmergencySpecialty.TRAUMA,
      onSelect = { onSelectSpecialty(EmergencySpecialty.TRAUMA) }
    )

    // Kinder-Notaufnahme (Pädiatrie)
    SpecialtyChip(
      specialty = EmergencySpecialty.PEDIATRIC,
      icon = Icons.Default.ChildCare,
      isSelected = selectedSpecialty == EmergencySpecialty.PEDIATRIC,
      onSelect = { onSelectSpecialty(EmergencySpecialty.PEDIATRIC) }
    )

    // Verbrennungszentrum
    SpecialtyChip(
      specialty = EmergencySpecialty.BURN,
      icon = Icons.Default.Whatshot,
      isSelected = selectedSpecialty == EmergencySpecialty.BURN,
      onSelect = { onSelectSpecialty(EmergencySpecialty.BURN) }
    )

    // ZNA
    SpecialtyChip(
      specialty = EmergencySpecialty.ZNA,
      icon = Icons.Default.Warning,
      isSelected = selectedSpecialty == EmergencySpecialty.ZNA,
      onSelect = { onSelectSpecialty(EmergencySpecialty.ZNA) }
    )
  }
}

@Composable
private fun SpecialtyChip(
  specialty: EmergencySpecialty,
  icon: ImageVector,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  FilterChip(
    selected = isSelected,
    onClick = onSelect,
    label = { Text(specialty.shortBadge, fontSize = 12.sp) },
    leadingIcon = {
      Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.padding(end = 4.dp)
      )
    },
    colors = FilterChipDefaults.filterChipColors(
      selectedContainerColor = MaterialTheme.colorScheme.primary,
      selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
      selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
    ),
    modifier = Modifier.testTag("filter_chip_${specialty.id}")
  )
}
