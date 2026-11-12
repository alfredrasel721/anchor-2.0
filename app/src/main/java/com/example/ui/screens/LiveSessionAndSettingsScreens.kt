package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AnchorScreen
import com.example.ui.components.BilateralTransparencyCard
import com.example.ui.components.ChildSelectorBar
import com.example.ui.components.PremiumGateBanner
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AnchorError
import com.example.ui.theme.AnchorPrimary
import com.example.ui.theme.AnchorSecondary
import com.example.ui.theme.StatusEnforced
import com.example.ui.theme.StatusEnforcedBg
import com.example.util.LiveMediaSessionManager
import com.example.viewmodel.AnchorUiState

// ============================================================================
// Notification & Distraction Filter [PREMIUM]
// ============================================================================
@Composable
fun NotificationFilterScreen(
    state: AnchorUiState,
    onSelectChild: (String) -> Unit,
    onUpdateConfig: (Boolean, String, Boolean, Boolean) -> Unit,
    onNavigateToPremium: () -> Unit,
    onInstantUnlockTrial: () -> Unit
) {
    val config = state.premiumConfig

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PremiumGateBanner(
            featureTitle = "Notification & Distraction Filtering",
            isPremiumActive = state.isPremiumActive,
            onUpgradeClick = onNavigateToPremium,
            onInstantUnlockTrial = onInstantUnlockTrial
        )

        ChildSelectorBar(
            children = state.children,
            selectedChildId = state.selectedChildId,
            onSelectChild = onSelectChild,
            onAddChildClick = {}
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Study & Bedtime Notification Shield", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Intercept non-essential pings during School Hours & Night Sleep",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = config.notificationFilterEnabled,
                        enabled = state.isPremiumActive,
                        onCheckedChange = {
                            onUpdateConfig(
                                it,
                                config.notificationProtocol,
                                config.batchMuteSocialDuringStudy,
                                config.blockGamingStreakPrompts
                            )
                        },
                        modifier = Modifier.testTag("toggle_notif_filter_master")
                    )
                }

                Text("Interception Protocol", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("BATCHING", "TOTAL_SILENCE", "EMERGENCY_ONLY").forEach { proto ->
                        FilterChip(
                            selected = config.notificationProtocol == proto,
                            enabled = state.isPremiumActive,
                            onClick = {
                                onUpdateConfig(
                                    config.notificationFilterEnabled,
                                    proto,
                                    config.batchMuteSocialDuringStudy,
                                    config.blockGamingStreakPrompts
                                )
                            },
                            label = { Text(proto.replace("_", " ")) },
                            modifier = Modifier.testTag("notif_proto_$proto")
                        )
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
                Text("Category Interception Policies", style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Batch & Mute Social Group Pings", style = MaterialTheme.typography.titleSmall)
                        Text("Delivers held messages in a calm 3:30 PM digest after school", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = config.batchMuteSocialDuringStudy,
                        enabled = state.isPremiumActive,
                        onCheckedChange = {
                            onUpdateConfig(
                                config.notificationFilterEnabled,
                                config.notificationProtocol,
                                it,
                                config.blockGamingStreakPrompts
                            )
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Suppress Manipulative Game 'Streak' Alerts", style = MaterialTheme.typography.titleSmall)
                        Text("Blocks FOMO & loot-box timer notifications during homework", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = config.blockGamingStreakPrompts,
                        enabled = state.isPremiumActive,
                        onCheckedChange = {
                            onUpdateConfig(
                                config.notificationFilterEnabled,
                                config.notificationProtocol,
                                config.batchMuteSocialDuringStudy,
                                it
                            )
                        }
                    )
                }
            }
        }

        BilateralTransparencyCard(
            title = "Zero-Break Safety Failsafe",
            description = "Calls and messages from Primary Guardians, Trusted Emergency Contacts, school nurses, and calendar alarms always bypass notification filtering."
        )
    }
}

// ============================================================================
// Active Live Session Viewer [PREMIUM]
// ============================================================================
@Composable
fun LiveSessionViewerScreen(
    state: AnchorUiState,
    onSelectChild: (String) -> Unit,
    onSelectMode: (String) -> Unit,
    onToggleTwoWayTalk: () -> Unit,
    onToggleSessionActive: () -> Unit,
    onToggleCameraFacing: () -> Unit = {},
    onCaptureSnapshot: () -> Unit = {},
    onPublishCameraFrame: (String, Int, Int) -> Unit = { _, _, _ -> },
    onNavigateToPremium: () -> Unit,
    onInstantUnlockTrial: () -> Unit
) {
    val child = state.selectedChild
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val remoteBitmap by LiveMediaSessionManager.remoteStreamBitmap.collectAsStateWithLifecycle()
    val localCameraBitmap by LiveMediaSessionManager.localCameraBitmap.collectAsStateWithLifecycle()
    val waveformBars by LiveMediaSessionManager.waveformBars.collectAsStateWithLifecycle()
    val audioLevelDb by LiveMediaSessionManager.audioLevelDb.collectAsStateWithLifecycle()
    val hardwareStatusLabel by LiveMediaSessionManager.hardwareStatusLabel.collectAsStateWithLifecycle()

    val mediaPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { /* Permissions granted or handled gracefully */ }

    LaunchedEffect(state.liveSessionActive, state.liveSessionMode) {
        if (state.liveSessionActive) {
            try {
                mediaPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                    )
                )
            } catch (_: Exception) {
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            LiveMediaSessionManager.stopAllMediaStreams()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PremiumGateBanner(
            featureTitle = "Audited Live Screen, Camera & Microphone Check-In",
            isPremiumActive = state.isPremiumActive,
            onUpgradeClick = onNavigateToPremium,
            onInstantUnlockTrial = onInstantUnlockTrial
        )

        ChildSelectorBar(
            children = state.children,
            selectedChildId = state.selectedChildId,
            onSelectChild = onSelectChild,
            onAddChildClick = {}
        )

        Surface(
            color = StatusEnforcedBg,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = StatusEnforced)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AUDITED LIVE SESSION ${state.premiumConfig.liveSessionAuditCode} • EXPLICIT CONSENT",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusEnforced
                        )
                        Text(
                            text = "Live check-ins require ${child.name}'s explicit consent on ${child.deviceName} and are logged in your family audit history.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                StatusBadge(state.liveSessionConsentStatus)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val modes = listOf(
                Triple("Screen View", Icons.Default.ScreenShare, "live_mode_screen"),
                Triple("Front Camera", Icons.Default.CameraFront, "live_mode_camera"),
                Triple("Ambient Audio", Icons.Default.GraphicEq, "live_mode_audio")
            )
            modes.forEach { (mode, icon, tag) ->
                val selected = state.liveSessionMode == mode
                Button(
                    onClick = { onSelectMode(mode) },
                    enabled = state.isPremiumActive,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected) AnchorPrimary else MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag(tag)
                ) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(mode.substringBefore(" "), style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D141F)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${state.liveSessionMode} • ${child.deviceName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Audio Level: ${audioLevelDb.toInt()} dBFS • Lens: ${state.liveSessionCameraFacing}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94F1D6)
                        )
                    }
                    StatusBadge(state.liveSessionConsentStatus)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF162235))
                        .testTag("live_session_media_viewport"),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.liveSessionActive && state.liveSessionConsentStatus == "CONSENT_GRANTED") {
                        when (state.liveSessionMode) {
                            "Front Camera" -> {
                                val previewView = remember { PreviewView(context) }
                                LaunchedEffect(state.liveSessionCameraFacing) {
                                    LiveMediaSessionManager.bindCameraXStream(
                                        context = context,
                                        lifecycleOwner = lifecycleOwner,
                                        previewView = previewView,
                                        useFrontCamera = state.liveSessionCameraFacing == "FRONT",
                                        onEncodedFrameReady = onPublishCameraFrame
                                    )
                                }
                                AndroidView(
                                    factory = { previewView },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .testTag("camerax_live_preview")
                                )
                                val fallbackBmp = localCameraBitmap ?: remoteBitmap
                                if (fallbackBmp != null) {
                                    Image(
                                        bitmap = fallbackBmp.asImageBitmap(),
                                        contentDescription = "Live Camera Stream",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            "Screen View" -> {
                                val displayBmp = remoteBitmap ?: localCameraBitmap
                                if (displayBmp != null) {
                                    Image(
                                        bitmap = displayBmp.asImageBitmap(),
                                        contentDescription = "Live Child Screen Mirror Stream",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .testTag("live_screen_bitmap_view")
                                    )
                                }
                            }
                            "Ambient Audio" -> {
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .testTag("live_audio_spectrum_canvas")
                                ) {
                                    val bars = waveformBars.size.coerceAtLeast(1)
                                    val barW = size.width / (bars * 1.75f)
                                    waveformBars.forEachIndexed { i, ratio ->
                                        val clamped = ratio.coerceIn(0.08f, 0.96f)
                                        val h = size.height * clamped * 0.78f
                                        drawRect(
                                            color = if (clamped > 0.55f) Color(0xFF34D399) else Color(0xFF94F1D6),
                                            topLeft = Offset(i * (barW * 1.75f) + 14f, (size.height - h) / 2f),
                                            size = Size(barW, h)
                                        )
                                    }
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Live Audio Stream (${audioLevelDb.toInt()} dBFS)",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (state.liveSessionTwoWayTalk) {
                                            "Two-Way Voice Intercom Active (Parent Mic + Speaker Open)"
                                        } else {
                                            "Listening to Audited Ambient Audio Track"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94F1D6)
                                    )
                                }
                            }
                        }

                        Surface(
                            color = Color.Black.copy(alpha = 0.62f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "LIVE • $hardwareStatusLabel • ${audioLevelDb.toInt()} dBFS",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94F1D6),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    } else {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val bars = 24
                            val barW = size.width / (bars * 1.8f)
                            for (i in 0 until bars) {
                                val h = size.height * 0.08f
                                drawRect(
                                    color = Color(0xFF94F1D6).copy(alpha = 0.25f),
                                    topLeft = Offset(i * (barW * 1.8f) + 16f, (size.height - h) / 2f),
                                    size = Size(barW, h)
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = when (state.liveSessionConsentStatus) {
                                    "AWAITING_CHILD_CONSENT" -> "Request Sent — Awaiting Consent (${state.liveSessionMode})"
                                    "ENDED" -> "Live Session Ended"
                                    else -> "Standby — Ready for Audited Check-In"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Stated Reason: \"${state.liveSessionReason}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94F1D6)
                            )
                        }
                    }
                }

                if (state.liveSessionActive) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (state.liveSessionMode == "Front Camera") {
                            OutlinedButton(
                                onClick = onToggleCameraFacing,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("flip_camera_lens_btn")
                            ) {
                                Icon(Icons.Default.Cameraswitch, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Flip Lens (${state.liveSessionCameraFacing})", color = Color.White)
                            }
                        }
                        if (state.liveSessionMode != "Ambient Audio") {
                            OutlinedButton(
                                onClick = onCaptureSnapshot,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("capture_live_snapshot_btn")
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save / Share Frame", color = Color.White)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onToggleTwoWayTalk,
                        enabled = state.isPremiumActive && state.liveSessionActive,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("toggle_two_way_talk_btn")
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.liveSessionTwoWayTalk) "Mute Two-Way Talk" else "Two-Way Talk",
                            color = Color.White
                        )
                    }
                    Button(
                        onClick = onToggleSessionActive,
                        enabled = state.isPremiumActive,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.liveSessionActive) AnchorError else AnchorSecondary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("toggle_live_session_btn")
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (state.liveSessionActive) "End Live Stream" else "Start Live Stream")
                    }
                }
            }
        }
    }
}

// ============================================================================
// Account Settings & Diagnostics
// ============================================================================
@Composable
fun AccountSettingsDiagnosticsScreen(
    state: AnchorUiState,
    onRunSelfTest: () -> Unit,
    onTogglePreference: (String) -> Unit,
    onExportSystemLog: () -> Unit,
    onNavigate: (AnchorScreen) -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            state.guardianDisplayName.ifBlank { "Household Guardian" },
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "${state.guardianEmail.ifBlank { "Not signed in" }} • ${state.familyHousehold.familyName.ifBlank { "My Family Household" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatusBadge(state.familyHousehold.subscriptionTier)
                }
                Text(
                    text = "Child Profiles: ${state.children.size} / 5 • Paired Devices: ${state.pairedDevices.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = AnchorPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onNavigate(AnchorScreen.PREMIUM_FEATURES) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Subscription Plans")
                    }
                    OutlinedButton(
                        onClick = { onNavigate(AnchorScreen.PRIVACY_EXPORT_DELETION) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Privacy & Data Rights")
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NetworkCheck, contentDescription = null, tint = AnchorPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Connection & System Health", style = MaterialTheme.typography.titleMedium)
                }

                PipelineRow("Family Cloud Synchronization", if (state.isCloudConnected) "ENFORCED" else "PENDING")
                PipelineRow("Offline Policy Queue (${state.pendingOfflineSyncCount} pending)", "ENFORCED")
                PipelineRow("Emergency SOS High-Priority Channel", "ENFORCED")
                PipelineRow("Paired Child Device Link", if (state.pairedDevices.isNotEmpty()) "VERIFIED" else "PENDING")

                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.lastSelfTestSummary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        color = AnchorSecondary
                    )
                }

                Button(
                    onClick = onRunSelfTest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_connection_self_test_btn")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Run Full Connection Self-Test")
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Parent App Controls & Preferences", style = MaterialTheme.typography.titleMedium)

                PrefSwitchRow(
                    title = "Require Biometrics / Screen Lock on Launch",
                    checked = state.biometricLockRequired,
                    onToggle = { onTogglePreference("BIOMETRIC") }
                )
                PrefSwitchRow(
                    title = "Critical SOS Alerts Override Android DND",
                    checked = state.criticalAlertsOverrideDnd,
                    onToggle = { onTogglePreference("DND_OVERRIDE") }
                )
                PrefSwitchRow(
                    title = "Dark Theme Display Mode",
                    checked = state.darkModeOverride,
                    onToggle = { onTogglePreference("DARK_MODE") }
                )
                PrefSwitchRow(
                    title = "Offline Policy Protection Cache",
                    checked = state.offlineDiagnosticsCacheEnabled,
                    onToggle = { onTogglePreference("OFFLINE_CACHE") }
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Recent Family Audit Log (${state.auditLogs.size})", style = MaterialTheme.typography.titleMedium)
                state.auditLogs.take(4).forEach { log ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("${log.eventCode} • ${log.actionTitle}", style = MaterialTheme.typography.labelLarge)
                            Text(log.details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                OutlinedButton(
                    onClick = onExportSystemLog,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_system_log_bundle_btn")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Diagnostic Log Bundle")
                }

                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sign_out_guardian_btn")
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = AnchorError)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out of Guardian Account", color = AnchorError)
                }
            }
        }
    }
}

@Composable
private fun PipelineRow(name: String, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, style = MaterialTheme.typography.bodyMedium)
        StatusBadge(status)
    }
}

@Composable
private fun PrefSwitchRow(title: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}
