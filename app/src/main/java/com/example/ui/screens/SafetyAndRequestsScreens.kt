package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.components.ChildSelectorBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AnchorError
import com.example.ui.theme.AnchorPrimary
import com.example.ui.theme.AnchorSecondary
import com.example.ui.theme.StatusEnforced
import com.example.ui.theme.StatusEnforcedBg
import com.example.viewmodel.AnchorUiState

// ============================================================================
// Location & Geofences
// ============================================================================
@Composable
fun LocationGeofencesScreen(
    state: AnchorUiState,
    onSelectChild: (String) -> Unit,
    onPingGps: () -> Unit,
    onOpenDirections: () -> Unit,
    onToggleGeofenceAlerts: (String) -> Unit,
    onAddGeofence: (String, Int, String) -> Unit,
    onDeleteGeofence: (String) -> Unit
) {
    val child = state.selectedChild
    var satelliteMode by remember { mutableStateOf(false) }
    var newZoneName by remember { mutableStateOf("") }
    var newZoneRadius by remember { mutableStateOf("200") }
    var newZoneSchedule by remember { mutableStateOf("Mon–Fri • 3:00 PM – 6:00 PM") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ChildSelectorBar(
            children = state.children,
            selectedChildId = state.selectedChildId,
            onSelectChild = onSelectChild,
            onAddChildClick = {}
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("LIVE SAFE-ZONE RADAR", style = MaterialTheme.typography.labelSmall)
                        Text("${child.name} • ${child.locationLabel}", style = MaterialTheme.typography.titleLarge)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = !satelliteMode,
                            onClick = { satelliteMode = false },
                            label = { Text("Map") }
                        )
                        FilterChip(
                            selected = satelliteMode,
                            onClick = { satelliteMode = true },
                            label = { Text("Sat") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (satelliteMode) Color(0xFF1A2E35) else Color(0xFFE6EEF8)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridColor = if (satelliteMode) Color.White.copy(alpha = 0.08f) else AnchorPrimary.copy(alpha = 0.08f)
                        for (i in 1..6) {
                            val y = size.height * (i / 7f)
                            drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 2f)
                            val x = size.width * (i / 7f)
                            drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 2f)
                        }
                        val center = Offset(size.width * 0.48f, size.height * 0.52f)
                        if (state.geofences.isNotEmpty()) {
                            drawCircle(
                                color = AnchorSecondary.copy(alpha = 0.20f),
                                radius = 68.dp.toPx(),
                                center = center
                            )
                            drawCircle(
                                color = AnchorSecondary,
                                radius = 68.dp.toPx(),
                                center = center,
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                        drawCircle(
                            color = Color.White,
                            radius = 14.dp.toPx(),
                            center = center
                        )
                        drawCircle(
                            color = AnchorPrimary,
                            radius = 10.dp.toPx(),
                            center = center
                        )
                    }

                    val activeZone = state.geofences.firstOrNull { it.activeInside }
                    Surface(
                        color = StatusEnforcedBg,
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (activeZone != null) {
                                "Inside ${activeZone.name} • ${activeZone.radiusMeters}m Perimeter"
                            } else {
                                child.locationLabel
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusEnforced,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onPingGps,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ping_gps_button")
                    ) {
                        Icon(Icons.Default.GpsFixed, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ping Live GPS")
                    }
                    OutlinedButton(
                        onClick = onOpenDirections,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("directions_to_child_button")
                    ) {
                        Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Directions")
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Configured Safe Geofences (${state.geofences.size})", style = MaterialTheme.typography.titleMedium)
                if (state.geofences.isEmpty()) {
                    Text(
                        "No safe zone boundaries configured yet. Add your home or school zone below to receive arrival and departure alerts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                state.geofences.forEach { geo ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (geo.activeInside) AnchorSecondary else AnchorPrimary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("${geo.name} (${geo.radiusMeters}m)", style = MaterialTheme.typography.titleSmall)
                                    Text(geo.triggerSummary, style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        geo.scheduleSummary,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = geo.alertsEnabled,
                                    onCheckedChange = { onToggleGeofenceAlerts(geo.geofenceId) },
                                    modifier = Modifier.testTag("toggle_geo_${geo.geofenceId}")
                                )
                                IconButton(
                                    onClick = { onDeleteGeofence(geo.geofenceId) },
                                    modifier = Modifier.testTag("delete_geo_${geo.geofenceId}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Geofence", tint = AnchorError)
                                }
                            }
                        }
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Create New Safe Zone Boundary", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newZoneName,
                        onValueChange = { newZoneName = it },
                        label = { Text("Zone Name (e.g. Home or School)") },
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("new_geofence_name_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newZoneRadius,
                        onValueChange = { newZoneRadius = it },
                        label = { Text("Radius (m)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.6f)
                            .testTag("new_geofence_radius_input"),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = newZoneSchedule,
                    onValueChange = { newZoneSchedule = it },
                    label = { Text("Active Window") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Button(
                    onClick = {
                        onAddGeofence(newZoneName, newZoneRadius.toIntOrNull() ?: 200, newZoneSchedule)
                        newZoneName = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_safe_geofence_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Safe Geofence")
                }
            }
        }
    }
}

// ============================================================================
// Alerts & SOS Activity
// ============================================================================
@Composable
fun AlertsSosActivityScreen(
    state: AnchorUiState,
    onTriggerSosDrill: () -> Unit,
    onAcknowledgeAlert: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All Events") }

    val filteredAlerts = state.alerts.filter { alert ->
        when (selectedFilter) {
            "Emergency & SOS" -> alert.category == "SOS"
            "Geofences" -> alert.category == "GEOFENCE"
            "Device Health" -> alert.category == "DEVICE_HEALTH"
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = StatusEnforced, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("SAFETY SENTINEL ACTIVE", style = MaterialTheme.typography.labelSmall, color = StatusEnforced)
                            Text("All Quiet & Protected", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    StatusBadge("ENFORCED")
                }

                Text(
                    text = "Emergency SOS overrides Android Do-Not-Disturb, locks high-accuracy GPS, and alerts Primary Guardians and Trusted Contacts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onTriggerSosDrill,
                    colors = ButtonDefaults.buttonColors(containerColor = AnchorError),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_family_sos_siren_btn")
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Run Family SOS Siren Drill")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All Events", "Emergency & SOS", "Geofences", "Device Health").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) }
                )
            }
        }

        filteredAlerts.forEach { alert ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (alert.category == "SOS") Icons.Default.Warning else Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (alert.category == "SOS") AnchorError else AnchorPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(alert.timestampLabel, style = MaterialTheme.typography.labelSmall)
                        }
                        StatusBadge(alert.severity)
                    }
                    Text("${alert.childName} • ${alert.title}", style = MaterialTheme.typography.titleMedium)
                    Text(
                        alert.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!alert.acknowledged) {
                        OutlinedButton(
                            onClick = { onAcknowledgeAlert(alert.alertId) },
                            modifier = Modifier.testTag("ack_alert_${alert.alertId}")
                        ) {
                            Text("Acknowledge Alert")
                        }
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val primaryNames = state.guardians.filter { it.isPrimaryGuardian }.joinToString(" & ") { it.name }
                    .ifBlank { state.guardianDisplayName.ifBlank { "Primary Household Guardians" } }
                val trustedNames = state.guardians.filter { !it.isPrimaryGuardian }.joinToString(", ") { it.name }
                    .ifBlank { "Trusted Emergency Contacts" }
                Text("Emergency SOS Escalation Chain", style = MaterialTheme.typography.titleMedium)
                Text("• Tier 1 (0s): $primaryNames (Critical Push + DND Override)", style = MaterialTheme.typography.bodySmall)
                Text("• Tier 2 (+60s unacknowledged): $trustedNames (Automated SMS + GPS Pin Link)", style = MaterialTheme.typography.bodySmall)
                Text("• Tier 3 (+120s unacknowledged): Backup Voice Call & High-Frequency Location Lock", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// ============================================================================
// Extra Time & App Requests
// ============================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExtraTimeAppRequestsScreen(
    state: AnchorUiState,
    onSelectChild: (String) -> Unit,
    onResolveRequest: (String, Boolean, Int, String) -> Unit,
    onToggleAutoApproveLearning: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ChildSelectorBar(
            children = state.children,
            selectedChildId = state.selectedChildId,
            onSelectChild = onSelectChild,
            onAddChildClick = {}
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Auto-Approve Verified School & Learning Apps", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Automatically approve age-appropriate educational and school learning applications.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.familyHousehold.autoApproveLearningApps,
                    onCheckedChange = { onToggleAutoApproveLearning() },
                    modifier = Modifier.testTag("toggle_auto_approve_learning")
                )
            }
        }

        if (state.requests.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("No Pending Requests", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "When your child requests extra screen time or a new app from their device, it will appear here for instant approval or decline.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        state.requests.forEach { req ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("${req.childName} • ${req.targetName}", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${req.targetCategory} • ${req.usedTodaySummary}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusBadge(req.status)
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"${req.childNote}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    if (req.status == "PENDING") {
                        if (req.requestType == "EXTRA_TIME") {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onResolveRequest(req.requestId, true, 15, "Approved +15m Bonus") },
                                    colors = ButtonDefaults.buttonColors(containerColor = AnchorSecondary),
                                    modifier = Modifier.testTag("approve_15m_${req.requestId}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Approve +15m")
                                }
                                OutlinedButton(
                                    onClick = { onResolveRequest(req.requestId, true, 30, "Approved +30m Bonus") }
                                ) {
                                    Text("+30m")
                                }
                                OutlinedButton(
                                    onClick = { onResolveRequest(req.requestId, false, 0, "Homework & dinner come first today") },
                                    modifier = Modifier.testTag("decline_${req.requestId}")
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Decline Gently")
                                }
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onResolveRequest(req.requestId, true, 0, "Allowed & Categorized as Learning") },
                                    colors = ButtonDefaults.buttonColors(containerColor = AnchorPrimary),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("approve_app_${req.requestId}")
                                ) {
                                    Text("Allow & Categorize Learning")
                                }
                                OutlinedButton(
                                    onClick = { onResolveRequest(req.requestId, false, 0, "Let's review this app together tonight") },
                                    modifier = Modifier.weight(0.6f)
                                ) {
                                    Text("Decline")
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Decision: ${req.decisionSummary} (${req.decidedBy})",
                            style = MaterialTheme.typography.labelLarge,
                            color = AnchorSecondary
                        )
                    }
                }
            }
        }
    }
}
