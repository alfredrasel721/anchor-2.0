package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.components.ChildSelectorBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AnchorPrimary
import com.example.ui.theme.AnchorSecondary
import com.example.ui.theme.AnchorTertiary
import com.example.viewmodel.AnchorUiState

// ============================================================================
// App Usage & Limits
// ============================================================================
@Composable
fun AppUsageLimitsScreen(
    state: AnchorUiState,
    onSelectChild: (String) -> Unit,
    onAdjustDailyAllowance: (Int) -> Unit,
    onToggleAppBlock: (String) -> Unit,
    onAdjustAppLimit: (String, Int) -> Unit,
    onAddCustomAppRule: (String, String, Int) -> Unit,
    onNavigateToSchedules: () -> Unit
) {
    val child = state.selectedChild
    var selectedTab by remember { mutableStateOf("App Categories") }
    var newAppName by remember { mutableStateOf("") }
    var newAppCategory by remember { mutableStateOf("Games & Entertainment") }
    var newAppLimit by remember { mutableStateOf("45") }

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("App Categories", "Individual Apps", "Downtime Schedule").forEach { tab ->
                val isSelected = selectedTab == tab
                Surface(
                    color = if (isSelected) AnchorPrimary else Color.Transparent,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (tab == "Downtime Schedule") {
                                onNavigateToSchedules()
                            } else {
                                selectedTab = tab
                            }
                        }
                ) {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                    )
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("GLOBAL DAILY ALLOWANCE", style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = "${child.dailyAllowanceMinutes / 60}h ${child.dailyAllowanceMinutes % 60}m / day",
                            style = MaterialTheme.typography.headlineMedium,
                            color = AnchorPrimary
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onAdjustDailyAllowance(-15) },
                            modifier = Modifier.testTag("allowance_minus_15m_btn")
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease 15m", modifier = Modifier.size(16.dp))
                            Text("15m")
                        }
                        Button(
                            onClick = { onAdjustDailyAllowance(15) },
                            modifier = Modifier.testTag("allowance_plus_15m_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase 15m", modifier = Modifier.size(16.dp))
                            Text("15m")
                        }
                    }
                }
                Text(
                    text = "Learning & Whitelisted apps remain accessible even after daily recreational screen allowance is reached.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val groupedRules = if (selectedTab == "App Categories") {
            state.appRules.groupBy { it.category }
        } else {
            mapOf("All Monitored Applications (${state.appRules.size})" to state.appRules)
        }

        if (state.appRules.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("No App Limits Configured Yet", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Add an application below to set a daily time cap or block it on ${child.name}'s device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            groupedRules.forEach { (categoryTitle, rules) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
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
                            Text(categoryTitle, style = MaterialTheme.typography.titleMedium)
                            StatusBadge("${rules.size} APPS")
                        }

                        rules.forEach { rule ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(rule.appName, style = MaterialTheme.typography.titleSmall)
                                            Text(
                                                text = "Used ${rule.usedMinutesToday}m today • Cap: ${rule.dailyLimitMinutes}m/day",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        StatusBadge(rule.status)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            OutlinedButton(
                                                onClick = { onAdjustAppLimit(rule.ruleId, -15) },
                                                modifier = Modifier.testTag("rule_minus_${rule.ruleId}")
                                            ) {
                                                Text("-15m")
                                            }
                                            OutlinedButton(
                                                onClick = { onAdjustAppLimit(rule.ruleId, 15) },
                                                modifier = Modifier.testTag("rule_plus_${rule.ruleId}")
                                            ) {
                                                Text("+15m")
                                            }
                                        }
                                        OutlinedButton(
                                            onClick = { onToggleAppBlock(rule.ruleId) },
                                            modifier = Modifier.testTag("toggle_block_${rule.ruleId}")
                                        ) {
                                            Icon(
                                                imageVector = if (rule.blocked) Icons.Default.CheckCircle else Icons.Default.Block,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (rule.blocked) "Unblock" else "Block App")
                                        }
                                    }
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
                Text("Add New App Limit Rule", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newAppName,
                        onValueChange = { newAppName = it },
                        label = { Text("App Name") },
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("new_app_rule_name_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newAppLimit,
                        onValueChange = { newAppLimit = it },
                        label = { Text("Cap (min)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.6f)
                            .testTag("new_app_rule_limit_input"),
                        singleLine = true
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Games & Entertainment", "Social & Communication", "School & Learning").forEach { cat ->
                        FilterChip(
                            selected = newAppCategory == cat,
                            onClick = { newAppCategory = cat },
                            label = { Text(cat.substringBefore(" ")) }
                        )
                    }
                }
                Button(
                    onClick = {
                        onAddCustomAppRule(newAppName, newAppCategory, newAppLimit.toIntOrNull() ?: 45)
                        newAppName = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_custom_app_rule_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save & Enforce App Rule")
                }
            }
        }
    }
}

// ============================================================================
// Schedules & Downtime
// ============================================================================
@Composable
fun SchedulesDowntimeScreen(
    state: AnchorUiState,
    onSelectChild: (String) -> Unit,
    onToggleSchedule: (String) -> Unit,
    onAddSchedule: (String, String, String, String) -> Unit,
    onInstantFocusPause: (String) -> Unit,
    onUpdateFairPlayNotice: (Int) -> Unit
) {
    val child = state.selectedChild
    var newTitle by remember { mutableStateOf("") }
    var newWindow by remember { mutableStateOf("8:30 PM — 7:00 AM") }
    var newDays by remember { mutableStateOf("Sun, Mon, Tue, Wed, Thu") }
    var newRuleDesc by remember { mutableStateOf("Phone calls, Emergency SOS & Alarm Clock only") }

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
                        Text("TODAY'S 24-HOUR RHYTHM", style = MaterialTheme.typography.labelSmall)
                        Text("Current Mode: ${child.currentMode}", style = MaterialTheme.typography.titleMedium)
                    }
                    StatusBadge("ENFORCED")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(999.dp))
                ) {
                    Box(modifier = Modifier.weight(0.29f).fillMaxSize().background(AnchorPrimary))
                    Box(modifier = Modifier.weight(0.30f).fillMaxSize().background(AnchorSecondary))
                    Box(modifier = Modifier.weight(0.22f).fillMaxSize().background(Color(0xFF94F1D6)))
                    Box(modifier = Modifier.weight(0.05f).fillMaxSize().background(AnchorTertiary))
                    Box(modifier = Modifier.weight(0.14f).fillMaxSize().background(AnchorPrimary))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("12 AM Sleep", style = MaterialTheme.typography.labelSmall)
                    Text("8 AM School", style = MaterialTheme.typography.labelSmall)
                    Text("3:15 PM Free", style = MaterialTheme.typography.labelSmall)
                    Text("8:30 PM Bed", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        if (state.schedules.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("No Recurring Schedules Yet", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Create a Bedtime, School Focus, or Family Dinner routine below to enforce automatic downtime windows.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            state.schedules.forEach { schedule ->
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
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.weight(1f)
                        ) {
                            val icon = when (schedule.iconType) {
                                "BEDTIME" -> Icons.Default.Bedtime
                                "SCHOOL" -> Icons.Default.School
                                "DINNER" -> Icons.Default.Restaurant
                                else -> Icons.Default.Schedule
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = AnchorPrimary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(schedule.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${schedule.windowDescription} • ${schedule.daysSummary}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = AnchorSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    schedule.ruleSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = schedule.enabled,
                            onCheckedChange = { onToggleSchedule(schedule.scheduleId) },
                            modifier = Modifier.testTag("toggle_schedule_${schedule.scheduleId}")
                        )
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
                Text("Instant Focus / Pause Block", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("30 min", "1 hour", "Until Bedtime").forEach { blockDur ->
                        OutlinedButton(
                            onClick = { onInstantFocusPause(blockDur) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("instant_focus_${blockDur.replace(" ", "_")}")
                        ) {
                            Text(blockDur)
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
                Text("Fair-Play Wind-Down Warning", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Give ${child.name} an advance heads-up banner before Bedtime or Downtime locks active games so they can save progress:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 10, 15).forEach { mins ->
                        FilterChip(
                            selected = state.familyHousehold.fairPlayNoticeMinutes == mins,
                            onClick = { onUpdateFairPlayNotice(mins) },
                            label = { Text("$mins min warning") }
                        )
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
                Text("Create Custom Routine Window", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Routine Name (e.g. Bedtime & Sleep)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_schedule_title_input"),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newWindow,
                        onValueChange = { newWindow = it },
                        label = { Text("Time Window") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newDays,
                        onValueChange = { newDays = it },
                        label = { Text("Days") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Button(
                    onClick = {
                        onAddSchedule(newTitle, newWindow, newDays, newRuleDesc)
                        newTitle = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_custom_schedule_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Recurring Routine")
                }
            }
        }
    }
}
