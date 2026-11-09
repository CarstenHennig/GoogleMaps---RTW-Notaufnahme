package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.Hospital
import com.example.ui.components.EmergencyFilterBar
import com.example.ui.components.HospitalCard
import com.example.ui.components.LocationStatusBar
import com.example.ui.components.PreNotificationDialog
import com.example.ui.components.RouteDetailSheet
import com.example.viewmodel.RtwViewModel
import com.example.viewmodel.SortMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RtwDashboardScreen(
  viewModel: RtwViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentLocation by viewModel.currentLocation.collectAsState()
  val isLiveGpsActive by viewModel.isLiveGpsActive.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedSpecialty by viewModel.selectedSpecialty.collectAsState()
  val sortMode by viewModel.sortMode.collectAsState()
  val isLoading by viewModel.isLoading.collectAsState()
  val selectedHospitalForRoute by viewModel.selectedHospitalForRoute.collectAsState()
  val currentRouteInfo by viewModel.currentRouteInfo.collectAsState()
  val isCalculatingRoute by viewModel.isCalculatingRoute.collectAsState()
  val showPreNotification by viewModel.showPreNotificationDialog.collectAsState()
  val preNotificationState by viewModel.preNotification.collectAsState()

  // Location Permission Launcher
  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (fineGranted || coarseGranted) {
      viewModel.startLiveGps { /* permission callback */ }
    }
  }

  fun checkAndStartLiveGps() {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
      if (isLiveGpsActive) {
        viewModel.stopLiveGps()
      } else {
        viewModel.startLiveGps {
          locationPermissionLauncher.launch(
            arrayOf(
              Manifest.permission.ACCESS_FINE_LOCATION,
              Manifest.permission.ACCESS_COARSE_LOCATION
            )
          )
        }
      }
    } else {
      locationPermissionLauncher.launch(
        arrayOf(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION
        )
      )
    }
  }

  // Handle hardware / gesture back navigation
  BackHandler(enabled = selectedHospitalForRoute != null || showPreNotification) {
    if (showPreNotification) {
      viewModel.closePreNotificationDialog()
    } else if (selectedHospitalForRoute != null) {
      viewModel.clearSelectedRoute()
    }
  }

  val filteredHospitals = viewModel.getFilteredHospitals()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.LocalHospital,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "RTW NOTAUFNAHME",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                letterSpacing = 0.5.sp,
                color = Color.White
              )
              Text(
                text = "Klinik-Navigator & PKW-Fahrstrecken",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.85f)
              )
            }
          }
        },
        actions = {
          // Sort mode toggle (Fahrzeit vs Luftlinie)
          IconButton(
            onClick = {
              viewModel.onSortModeChanged(
                if (sortMode == SortMode.DRIVING_TIME) SortMode.DISTANCE else SortMode.DRIVING_TIME
              )
            },
            modifier = Modifier.testTag("toggle_sort_mode_button")
          ) {
            Icon(
              imageVector = Icons.Default.Sort,
              contentDescription = "Sortierung ändern",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. Paramedic Location & Telemetry Status Bar
      LocationStatusBar(
        location = currentLocation,
        isLiveGpsActive = isLiveGpsActive,
        presets = viewModel.locationTracker.presets,
        onToggleGps = { checkAndStartLiveGps() },
        onSelectPreset = { preset -> viewModel.selectPreset(preset) },
        onRefreshHospitals = { viewModel.refreshHospitals() },
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
      )

      // 2. Search & Sort Indicator Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.onSearchQueryChanged(it) },
          placeholder = { Text("Klinik, Notaufnahme oder Stadt suchen...", fontSize = 13.sp) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Löschen", modifier = Modifier.size(18.dp))
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("search_hospital_input")
        )
      }

      // 3. Emergency Specialty Filter Bar (ZNA, CPU, Stroke, Trauma, etc.)
      EmergencyFilterBar(
        selectedSpecialty = selectedSpecialty,
        onSelectSpecialty = { viewModel.onSpecialtyFilterSelected(it) }
      )

      // 4. Header Bar: Hospital count & Active Sort
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${filteredHospitals.size} Notaufnahmen im Umkreis",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = "Sortierung: ${sortMode.label}",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )
      }

      // 5. Hospital Cards List
      Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading && filteredHospitals.isEmpty()) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.height(12.dp))
              Text("Ermittle Notaufnahmen & PKW-Routen...", fontSize = 14.sp)
            }
          }
        } else if (filteredHospitals.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.LocalHospital,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = Color.LightGray
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Keine Notaufnahmen für Filter gefunden",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Setze den Suchbegriff oder Fachrichtungsfilter zurück, um alle Kliniken anzuzeigen.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .testTag("hospitals_lazy_column"),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(
              items = filteredHospitals,
              key = { it.id }
            ) { hospital ->
              HospitalCard(
                hospital = hospital,
                onStartNavigation = { viewModel.launchTurnByTurnNavigation(hospital) },
                onShowRoute = { viewModel.selectHospitalForRoute(hospital) },
                onCallEmergency = { viewModel.callHospitalEmergency(hospital) },
                onOpenPreNotification = { viewModel.openPreNotificationDialog(hospital) },
                onToggleStatus = { viewModel.toggleHospitalBedStatus(hospital.id) }
              )
            }

            item {
              Spacer(modifier = Modifier.height(16.dp))
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "Hinweis für RTW-Fahrten: 100% kostenfrei, OpenStreetMap & OSRM basierter Dienst. Sondersignalfahrten können die reelle Fahrzeit verkürzen.",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(12.dp),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
              }
              Spacer(modifier = Modifier.height(32.dp))
            }
          }
        }
      }
    }
  }

  // 6. Route Detail Sheet
  selectedHospitalForRoute?.let { hospital ->
    RouteDetailSheet(
      hospital = hospital,
      routeInfo = currentRouteInfo,
      isLoading = isCalculatingRoute,
      onDismiss = { viewModel.clearSelectedRoute() },
      onStartNavigation = { viewModel.launchTurnByTurnNavigation(hospital) },
      onCallEmergency = { viewModel.callHospitalEmergency(hospital) }
    )
  }

  // 7. Pre-Notification Dialog
  if (showPreNotification) {
    PreNotificationDialog(
      hospital = selectedHospitalForRoute,
      state = preNotificationState,
      onUpdate = { update -> viewModel.updatePreNotification(update) },
      onDismiss = { viewModel.closePreNotificationDialog() },
      onShare = { viewModel.shareVoranmeldung(selectedHospitalForRoute) },
      onCall = { selectedHospitalForRoute?.let { viewModel.callHospitalEmergency(it) } }
    )
  }
}
