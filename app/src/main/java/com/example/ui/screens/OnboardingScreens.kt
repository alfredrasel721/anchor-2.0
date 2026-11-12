package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BilateralTransparencyCard
import com.example.ui.components.ChildSelectorBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AnchorPrimary
import com.example.ui.theme.AnchorPrimaryContainer
import com.example.ui.theme.AnchorSecondary
import com.example.ui.theme.AnchorSecondaryContainer
import com.example.ui.theme.StatusEnforced
import com.example.ui.theme.StatusEnforcedBg
import com.example.viewmodel.AnchorUiState

// ============================================================================
// Welcome & Sign In
// ============================================================================
@Composable
fun WelcomeSignInScreen(
    state: AnchorUiState,
    onGoogleSignIn: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.linearGradient(
                        listOf(AnchorPrimary, AnchorPrimaryContainer)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = AnchorSecondaryContainer,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Anchor Emblem",
                            tint = AnchorSecondary,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(28.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ANCHOR FAMILY SYSTEM",
                            style = MaterialTheme.typography.labelSmall,
                            color = AnchorSecondaryContainer
                        )
                        Text(
                            text = "Anchor Parent",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Calm, Transparent Digital Guardianship.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Built on mutual family trust rather than covert surveillance. Manage screen allowances, bedtime schedules, safe school zones, and SOS protection in one place.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.88f)
                )
            }
        }

        BilateralTransparencyCard(
            title = "The Anchor Family Pledge",
            description = "Every rule, schedule, and safety check is visible to both parent and child. No hidden spyware, no secret keylogging, and explicit child consent for any live safety check-in."
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Guardian Sign In",
                        style = MaterialTheme.typography.titleLarge
                    )
                    StatusBadge("VERIFIED")
                }

                Text(
                    text = "Sign in with your Google account to create or access your family household and manage your children's connected devices.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onGoogleSignIn,
                    enabled = !state.authLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("google_sign_in_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AnchorPrimary)
                ) {
                    if (state.authLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Signing in with Google...")
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google Guardian Account",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        Surface(
            color = StatusEnforcedBg,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = StatusEnforced
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Private & Encrypted Family Protection",
                        style = MaterialTheme.typography.labelLarge,
                        color = StatusEnforced
                    )
                    Text(
                        text = "Your family data is strictly isolated to your authenticated Google Guardian account and never shared with advertisers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ============================================================================
// Family Onboarding
// ============================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FamilyOnboardingScreen(
    state: AnchorUiState,
    onSaveHousehold: (String, String, String, String) -> Unit,
    onAddChild: (String, Int, String, String) -> Unit,
    onProceedToPairing: () -> Unit
) {
    var familyName by remember(state.familyHousehold.familyName) {
        mutableStateOf(state.familyHousehold.familyName)
    }
    var guardianName by remember(state.guardianDisplayName) {
        mutableStateOf(state.guardianDisplayName)
    }
    var selectedRole by remember(state.guardianRoleChip) {
        mutableStateOf(state.guardianRoleChip)
    }
    var selectedCadence by remember(state.notificationCadence) {
        mutableStateOf(state.notificationCadence)
    }

    var newChildName by remember { mutableStateOf("") }
    var newChildAge by remember { mutableStateOf("10") }
    var newChildGrade by remember { mutableStateOf("5th Grade") }
    var newChildDevice by remember { mutableStateOf("Android Device") }

    var pledgeTransparent by remember { mutableStateOf(true) }
    var pledgeEmergencyAlwaysOn by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "STEP 1 OF 2 • HOUSEHOLD SETUP",
                        style = MaterialTheme.typography.labelSmall,
                        color = AnchorPrimary
                    )
                    Text(
                        text = "Configure Family & Guardian Identity",
                        style = MaterialTheme.typography.titleMedium,
                        color = AnchorPrimary
                    )
                }
                StatusBadge("ACTIVE")
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "1. Household & Primary Guardian",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    value = familyName,
                    onValueChange = { familyName = it },
                    label = { Text("Family Household Name") },
                    placeholder = { Text("Enter your family name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_family_name_input"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = guardianName,
                    onValueChange = { guardianName = it },
                    label = { Text("Primary Guardian Display Name") },
                    placeholder = { Text("Your Full Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_guardian_name_input"),
                    singleLine = true
                )

                Text(
                    text = "Guardian Family Role",
                    style = MaterialTheme.typography.labelLarge
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Mother", "Father", "Legal Guardian", "Co-Parent").forEach { role ->
                        FilterChip(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role },
                            label = { Text(role) }
                        )
                    }
                }

                Text(
                    text = "Notification Cadence",
                    style = MaterialTheme.typography.labelLarge
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All Safety & Requests", "Critical & Requests Only", "Daily Digest + SOS").forEach { cad ->
                        FilterChip(
                            selected = selectedCadence == cad,
                            onClick = { selectedCadence = cad },
                            label = { Text(cad) }
                        )
                    }
                }

                Button(
                    onClick = { onSaveHousehold(familyName, guardianName, selectedRole, selectedCadence) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_household_profile_btn")
                ) {
                    Text("Save Household Profile")
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
                Text(
                    text = "2. Children in Household (${state.children.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                if (state.children.isEmpty()) {
                    Text(
                        text = "Add your child below to generate their secure pairing code and customize their daily screen boundaries.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                state.children.forEach { child ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${child.name} (Age ${child.age})",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "${child.gradeLabel} • ${child.deviceName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StatusBadge(child.syncStatus)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Add Child Profile",
                    style = MaterialTheme.typography.labelLarge,
                    color = AnchorPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newChildName,
                        onValueChange = { newChildName = it },
                        label = { Text("Child Name") },
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("new_child_name_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newChildAge,
                        onValueChange = { newChildAge = it },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.6f)
                            .testTag("new_child_age_input"),
                        singleLine = true
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newChildGrade,
                        onValueChange = { newChildGrade = it },
                        label = { Text("Grade / Class") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newChildDevice,
                        onValueChange = { newChildDevice = it },
                        label = { Text("Target Device") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                OutlinedButton(
                    onClick = {
                        onAddChild(
                            newChildName,
                            newChildAge.toIntOrNull() ?: 10,
                            newChildGrade,
                            newChildDevice
                        )
                        newChildName = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_add_child_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Child to Household Roster")
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
                Text(
                    text = "3. Bilateral Family Trust Charter",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = pledgeTransparent,
                        onCheckedChange = { pledgeTransparent = it }
                    )
                    Text(
                        text = "I agree to discuss screen boundaries openly with my child; all active rules will be shown clearly on their device.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = pledgeEmergencyAlwaysOn,
                        onCheckedChange = { pledgeEmergencyAlwaysOn = it }
                    )
                    Text(
                        text = "Emergency SOS, emergency dialer, and Primary Guardian calls will never be blocked during downtime.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        onSaveHousehold(familyName, guardianName, selectedRole, selectedCadence)
                        if (newChildName.isNotBlank()) {
                            onAddChild(
                                newChildName,
                                newChildAge.toIntOrNull() ?: 10,
                                newChildGrade,
                                newChildDevice
                            )
                            newChildName = ""
                        }
                        onProceedToPairing()
                    },
                    enabled = pledgeTransparent && pledgeEmergencyAlwaysOn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("continue_to_pair_code_btn"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Save & Continue to Pair Child Device")
                }
            }
        }
    }
}

// ============================================================================
// Add Child & Pair Device
// ============================================================================
@Composable
fun AddChildPairCodeScreen(
    state: AnchorUiState,
    onSelectChild: (String) -> Unit,
    onGenerateNewCode: () -> Unit,
    onRevokeCode: () -> Unit = {},
    onCopyCode: () -> Unit,
    onShareInstructions: () -> Unit,
    onFinishToDashboard: () -> Unit
) {
    var showQrMatrix by remember { mutableStateOf(false) }
    val selectedChild = state.selectedChild
    val isCodeRevoked = state.pairingCode.revoked || state.pairingCode.status == "REVOKED"
    val isCodeConsumed = state.pairingCode.status == "CONSUMED"

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
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            text = "PAIRING FOR ${selectedChild.name.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AnchorPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                    StatusBadge(
                        when {
                            isCodeRevoked -> "REVOKED"
                            isCodeConsumed -> "CONSUMED"
                            else -> "VALID 10M"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(4.dp)
                ) {
                    Surface(
                        color = if (!showQrMatrix) AnchorPrimary else Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showQrMatrix = false }
                            .testTag("pair_mode_passcode_tab")
                    ) {
                        Text(
                            text = "6-Digit Passcode",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (!showQrMatrix) Color.White else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                    Surface(
                        color = if (showQrMatrix) AnchorPrimary else Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showQrMatrix = true }
                            .testTag("pair_mode_qr_tab")
                    ) {
                        Text(
                            text = "QR Handshake Matrix",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (showQrMatrix) Color.White else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (!showQrMatrix) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, AnchorPrimary.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                            .padding(vertical = 24.dp, horizontal = 16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isCodeRevoked) "REVOKED" else state.pairingCode.code,
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 42.sp,
                                    letterSpacing = 6.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = if (isCodeRevoked) MaterialTheme.colorScheme.error else AnchorPrimary,
                                modifier = Modifier.testTag("pairing_code_display")
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isCodeRevoked) {
                                    "Code invalidated by Guardian • Tap 'New Code' to issue a fresh code"
                                } else {
                                    "One-Time Security Code (Valid for 10 minutes) • Target: ${selectedChild.deviceName}"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .size(210.dp)
                            .border(2.dp, AnchorPrimary, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        val codeSeed = state.pairingCode.code.hashCode()
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val grid = 11
                            val cellW = size.width / grid
                            val cellH = size.height / grid
                            for (r in 0 until grid) {
                                for (c in 0 until grid) {
                                    val isFinder = (r < 3 && c < 3) || (r < 3 && c >= grid - 3) || (r >= grid - 3 && c < 3)
                                    val bit = ((codeSeed shr ((r * grid + c) % 24)) and 1) == 1
                                    if (isFinder || bit) {
                                        drawRect(
                                            color = AnchorPrimary,
                                            topLeft = Offset(c * cellW, r * cellH),
                                            size = Size(cellW * 0.9f, cellH * 0.9f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Scan from Anchor Child App on ${selectedChild.deviceName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCopyCode,
                        enabled = !isCodeRevoked,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_pairing_code_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy")
                    }
                    OutlinedButton(
                        onClick = onGenerateNewCode,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("refresh_pairing_code_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Code")
                    }
                    OutlinedButton(
                        onClick = onRevokeCode,
                        enabled = !isCodeRevoked,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("revoke_pairing_code_btn")
                    ) {
                        Text("Revoke", color = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onShareInstructions,
                    enabled = !isCodeRevoked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("share_pairing_link_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = AnchorSecondary)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Pairing Invite")
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
                Text(
                    text = "Paired Child Devices (${state.pairedDevices.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                if (state.pairedDevices.isEmpty()) {
                    Text(
                        text = "No Child Companion device has completed pairing yet. Enter code ${state.pairingCode.code} in Anchor Child to link.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    state.pairedDevices.forEach { dev ->
                        Surface(
                            color = StatusEnforcedBg,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("${dev.childName} • ${dev.deviceName}", style = MaterialTheme.typography.titleSmall)
                                    Text("Code: ${dev.redeemedCode}", style = MaterialTheme.typography.bodySmall)
                                }
                                StatusBadge(dev.status)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhonelinkSetup, contentDescription = null, tint = AnchorPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "How Child Device Pairing Works",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = "1. Install Anchor Child Companion on ${selectedChild.name}'s Android device.\n" +
                        "2. Enter passcode ${state.pairingCode.code} or scan the QR matrix above.\n" +
                        "3. Review the transparent Family Charter together with ${selectedChild.name} and grant Usage Stats, Accessibility & Location permissions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onFinishToDashboard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("go_to_dashboard_from_pairing_btn")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continue to Family Dashboard")
                }
            }
        }
    }
}
