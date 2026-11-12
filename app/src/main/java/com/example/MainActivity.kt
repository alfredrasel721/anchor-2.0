package com.example

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.auth.AuthManager
import com.example.data.local.AnchorLocalDatabase
import com.example.data.remote.AnchorRepository
import com.example.model.AnchorScreen
import com.example.ui.components.StitchBottomNavBar
import com.example.ui.components.StitchTopAppBar
import com.example.ui.screens.AccountSettingsDiagnosticsScreen
import com.example.ui.screens.AddChildPairCodeScreen
import com.example.ui.screens.AlertsSosActivityScreen
import com.example.ui.screens.AppUsageLimitsScreen
import com.example.ui.screens.ChildProfileDeviceStatusScreen
import com.example.ui.screens.ExtraTimeAppRequestsScreen
import com.example.ui.screens.FamilyChatTasksScreen
import com.example.ui.screens.FamilyDashboardScreen
import com.example.ui.screens.FamilyOnboardingScreen
import com.example.ui.screens.GuardianRolesTrustedContactsScreen
import com.example.ui.screens.LiveSessionViewerScreen
import com.example.ui.screens.LocationGeofencesScreen
import com.example.ui.screens.NotificationFilterScreen
import com.example.ui.screens.PremiumFeaturesScreen
import com.example.ui.screens.PrivacyExportDeletionScreen
import com.example.ui.screens.SchedulesDowntimeScreen
import com.example.ui.screens.ScreenTimeAnalyticsScreen
import com.example.ui.screens.SocialAppContentFiltersScreen
import com.example.ui.screens.WebFilterRulesScreen
import com.example.ui.screens.WelcomeSignInScreen
import com.example.ui.theme.AnchorParentTheme
import com.example.ui.theme.AnchorPrimary
import com.example.util.BillingManager
import com.example.util.LiveMediaSessionManager
import com.example.viewmodel.AnchorParentViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

class MainActivity : ComponentActivity() {
    internal lateinit var parentViewModel: AnchorParentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeFirebaseSafely()
        enableEdgeToEdge()

        val repository = AnchorRepository.create(applicationContext)
        val localDao = AnchorLocalDatabase.getInstance(applicationContext).anchorLocalDao()
        val authManager = AuthManager()

        parentViewModel = ViewModelProvider(
            this,
            AnchorParentViewModel.Factory(
                repository = repository,
                localDao = localDao,
                authManager = authManager
            )
        )[AnchorParentViewModel::class.java]

        setContent {
            val uiState by parentViewModel.uiState.collectAsStateWithLifecycle()

            AnchorParentTheme(darkTheme = uiState.darkModeOverride) {
                AnchorParentRoot(viewModel = parentViewModel)
            }
        }
    }

    private fun initializeFirebaseSafely() {
        try {
            FirebaseApp.initializeApp(this)
            val appCheck = FirebaseAppCheck.getInstance()
            appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
        } catch (_: Exception) {
            // Safe fallback in unit test or uninitialized environment
        }
    }
}

@Composable
fun AnchorParentRoot(viewModel: AnchorParentViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val billingManager = remember(context) {
        BillingManager(context.applicationContext) { tier, productId, token, statusNote ->
            viewModel.onPlayBillingEntitlementVerified(tier, productId, token, statusNote)
        }
    }

    DisposableEffect(billingManager) {
        billingManager.startConnection()
        onDispose {
            billingManager.endConnection()
        }
    }

    // Request runtime notification/location permissions gracefully when signed in
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { /* Handled gracefully */ }

    LaunchedEffect(state.isSignedIn) {
        if (state.isSignedIn) {
            val perms = mutableListOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                perms.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            try {
                permissionLauncher.launch(perms.toTypedArray())
            } catch (_: Exception) {
                // Ignore if ActivityResultRegistry not active in test harness
            }
        }
    }

    // Mandatory BackHandler for state-switched navigation
    BackHandler(enabled = state.isSignedIn && (state.backStack.isNotEmpty() || state.currentScreen != AnchorScreen.FAMILY_DASHBOARD)) {
        if (!viewModel.navigateBack()) {
            if (state.currentScreen != AnchorScreen.WELCOME_SIGN_IN &&
                state.currentScreen != AnchorScreen.FAMILY_DASHBOARD
            ) {
                viewModel.navigateTo(AnchorScreen.FAMILY_DASHBOARD)
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (state.isSignedIn && state.currentScreen != AnchorScreen.WELCOME_SIGN_IN) {
                StitchTopAppBar(
                    currentScreen = state.currentScreen,
                    familyName = state.familyHousehold.familyName,
                    isPremiumActive = state.isPremiumActive,
                    canNavigateBack = state.backStack.isNotEmpty(),
                    onBack = { viewModel.navigateBack() },
                    onOpenPremium = { viewModel.navigateTo(AnchorScreen.PREMIUM_FEATURES) }
                )
            }
        },
        bottomBar = {
            if (state.isSignedIn && state.currentScreen != AnchorScreen.WELCOME_SIGN_IN) {
                StitchBottomNavBar(
                    currentScreen = state.currentScreen,
                    pendingRequestsCount = state.pendingRequestsCount,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Interactive Feedback Banner
            state.statusBannerMessage?.let { msg ->
                Surface(
                    color = AnchorPrimary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("status_feedback_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearBannerMessage() },
                            modifier = Modifier.clickable { viewModel.clearBannerMessage() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss Notice",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (state.currentScreen) {
                    AnchorScreen.WELCOME_SIGN_IN -> WelcomeSignInScreen(
                        state = state,
                        onGoogleSignIn = { viewModel.signInWithGoogle(context) }
                    )
                    AnchorScreen.FAMILY_ONBOARDING -> FamilyOnboardingScreen(
                        state = state,
                        onSaveHousehold = { fam, guard, role, cad ->
                            viewModel.updateOnboardingHousehold(fam, guard, role, cad)
                        },
                        onAddChild = { name, age, grade, dev ->
                            viewModel.addChildToHousehold(name, age, grade, dev)
                        },
                        onProceedToPairing = { viewModel.navigateTo(AnchorScreen.ADD_CHILD_PAIR_CODE) }
                    )
                    AnchorScreen.ADD_CHILD_PAIR_CODE -> AddChildPairCodeScreen(
                        state = state,
                        onSelectChild = { viewModel.selectChild(it) },
                        onGenerateNewCode = { viewModel.generateNewPairingCode() },
                        onRevokeCode = { viewModel.revokeCurrentPairingCode() },
                        onCopyCode = { viewModel.copyPairingCodeToClipboard(context) },
                        onShareInstructions = { viewModel.sharePairingInstructions(context) },
                        onFinishToDashboard = { viewModel.finishOnboardingToDashboard() }
                    )
                    AnchorScreen.FAMILY_DASHBOARD -> FamilyDashboardScreen(
                        state = state,
                        onSelectChild = { viewModel.selectChild(it) },
                        onNavigate = { viewModel.navigateTo(it) },
                        onTogglePauseDevice = { viewModel.togglePauseChildDevice(it) },
                        onGrantBonusMinutes = { viewModel.grantBonusMinutes(it) },
                        onResolveRequest = { id, ok, mins, summary ->
                            viewModel.resolveChildRequest(id, ok, mins, summary)
                        }
                    )
                    AnchorScreen.CHILD_PROFILE_DEVICE -> ChildProfileDeviceStatusScreen(
                        state = state,
                        onSelectChild = { viewModel.selectChild(it) },
                        onQuickAction = { viewModel.triggerDeviceQuickAction(it) },
                        onTogglePauseDevice = { viewModel.togglePauseChildDevice(it) },
                        onPairReplacementDevice = { viewModel.navigateTo(AnchorScreen.ADD_CHILD_PAIR_CODE) }
                    )
                    AnchorScreen.SCREEN_TIME_ANALYTICS -> ScreenTimeAnalyticsScreen(
                        state = state,
                        onSelectChild = { viewModel.selectChild(it) },
                        onSelectRange = { viewModel.setAnalyticsRange(it) },
                        onExportReport = { viewModel.exportAnalyticsReport(context) },
                        onNavigateToAppLimits = { viewModel.navigateTo(AnchorScreen.APP_USAGE_LIMITS) }
                    )
                    AnchorScreen.APP_USAGE_LIMITS -> AppUsageLimitsScreen(
                        state = state,
                        onSelectChild = { viewModel.selectChild(it) },
                        onAdjustDailyAllowance = { viewModel.adjustDailyAllowance(it) },
                        onToggleAppBlock = { viewModel.toggleAppBlock(it) },
                        onAdjustAppLimit = { id, delta -> viewModel.adjustAppRuleLimit(id, delta) },
                        onAddCustomAppRule = { name, cat, lim -> viewModel.addCustomAppRule(name, cat, lim) },
                        onNavigateToSchedules = { viewModel.navigateTo(AnchorScreen.SCHEDULES_DOWNTIME) }
                    )
                    AnchorScreen.SCHEDULES_DOWNTIME -> SchedulesDowntimeScreen(
                        state = state,
                        onSelectChild = { viewModel.selectChild(it) },
                        onToggleSchedule = { viewModel.toggleScheduleRoutine(it) },
                        onAddSchedule = { t, w, d, r -> viewModel.addCustomSchedule(t, w, d, r) },
                        onInstantFocusPause = { viewModel.togglePauseChildDevice(it) },
                        onUpdateFairPlayNotice = { viewModel.updateFairPlayNoticeMinutes(it) }
                    )
                    AnchorScreen.LOCATION_GEOFENCES -> LocationGeofencesScreen(
                        state = state,
                        onSelectChild = { viewModel.selectChild(it) },
                        onPingGps = { viewModel.pingChildGps(context) },
                        onOpenDirections = { viewModel.openDirectionsToChild(context) },
                        onToggleGeofenceAlerts = { viewModel.toggleGeofenceAlerts(it) },
                        onAddGeofence = { n, r, s -> viewModel.addSafeGeofence(n, r, s) },
                        onDeleteGeofence = { viewModel.deleteSafeGeofence(it) }
                    )
                    AnchorScreen.ALERTS_SOS_ACTIVITY -> AlertsSosActivityScreen(
                        state = state,
                        onTriggerSosDrill = { viewModel.triggerTestSosSirenDrill(context) },
                        onAcknowledgeAlert = { viewModel.acknowledgeAlert(it) }
                    )
                    AnchorScreen.EXTRA_TIME_REQUESTS -> ExtraTimeAppRequestsScreen(
                        state = state,
                        onSelectChild = { viewModel.selectChild(it) },
                        onResolveRequest = { id, ok, mins, summary ->
                            viewModel.resolveChildRequest(id, ok, mins, summary)
                        },
                        onToggleAutoApproveLearning = { viewModel.toggleAutoApproveLearningApps() }
                    )
                    AnchorScreen.FAMILY_CHAT_TASKS -> FamilyChatTasksScreen(
                        state = state,
                        onSendMessage = { viewModel.sendChatMessage(it) },
                        onAssignTask = { t, r, d -> viewModel.assignFamilyTask(t, r, d) },
                        onApproveTask = { viewModel.approveFamilyTask(it) }
                    )
                    AnchorScreen.GUARDIAN_ROLES_CONTACTS -> GuardianRolesTrustedContactsScreen(
                        state = state,
                        onToggleGeofenceAlerts = { viewModel.toggleGuardianGeofenceAlerts(it) },
                        onInviteContact = { n, r, e, p -> viewModel.inviteGuardianOrContact(n, r, e, p) }
                    )
                    AnchorScreen.PRIVACY_EXPORT_DELETION -> PrivacyExportDeletionScreen(
                        state = state,
                        onExportJsonArchive = { viewModel.exportHouseholdDataJson(context) },
                        onPurgeHistoricalLogs = { viewModel.purgeHistoricalLogs() },
                        onToggleAnonymousTelemetry = { viewModel.toggleAnonymousTelemetry() },
                        onDeleteHousehold = { viewModel.initiateFamilyDeletion() }
                    )
                    AnchorScreen.PREMIUM_FEATURES -> PremiumFeaturesScreen(
                        state = state,
                        onSetSubscriptionTier = { tier, active -> viewModel.setSubscriptionTier(tier, active) },
                        onLaunchPlayBilling = { tier ->
                            viewModel.launchPlayBillingPurchase(context as? Activity, billingManager, tier)
                        },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    AnchorScreen.WEB_FILTER_RULES -> WebFilterRulesScreen(
                        state = state,
                        onSelectChild = { viewModel.selectChild(it) },
                        onSelectProfile = { viewModel.updateWebFilterProfile(it) },
                        onToggleCategory = { viewModel.toggleWebBlockedCategory(it) },
                        onAddWhitelist = { viewModel.addWhitelistedDomain(it) },
                        onRemoveWhitelist = { viewModel.removeWhitelistedDomain(it) },
                        onAddBlacklist = { viewModel.addBlacklistedDomain(it) },
                        onRemoveBlacklist = { viewModel.removeBlacklistedDomain(it) },
                        onNavigateToPremium = { viewModel.navigateTo(AnchorScreen.PREMIUM_FEATURES) },
                        onInstantUnlockTrial = {
                            viewModel.launchPlayBillingPurchase(context as? Activity, billingManager, "PREMIUM_ANNUAL")
                        }
                    )
                    AnchorScreen.SOCIAL_CONTENT_FILTERS -> SocialAppContentFiltersScreen(
                        state = state,
                        onSelectChild = { viewModel.selectChild(it) },
                        onSelectShieldProfile = { viewModel.updateSocialShieldProfile(it) },
                        onTogglePlatformRule = { viewModel.toggleSocialPlatformRule(it) },
                        onNavigateToPremium = { viewModel.navigateTo(AnchorScreen.PREMIUM_FEATURES) },
                        onInstantUnlockTrial = {
                            viewModel.launchPlayBillingPurchase(context as? Activity, billingManager, "PREMIUM_ANNUAL")
                        }
                    )
                    AnchorScreen.NOTIFICATION_FILTER -> NotificationFilterScreen(
                        state = state,
                        onSelectChild = { viewModel.selectChild(it) },
                        onUpdateConfig = { en, proto, batch, streak ->
                            viewModel.updateNotificationFilterConfig(en, proto, batch, streak)
                        },
                        onNavigateToPremium = { viewModel.navigateTo(AnchorScreen.PREMIUM_FEATURES) },
                        onInstantUnlockTrial = {
                            viewModel.launchPlayBillingPurchase(context as? Activity, billingManager, "PREMIUM_ANNUAL")
                        }
                    )
                    AnchorScreen.LIVE_SESSION_VIEWER -> LiveSessionViewerScreen(
                        state = state,
                        onSelectChild = { viewModel.selectChild(it) },
                        onSelectMode = { viewModel.setLiveSessionMode(it, context) },
                        onToggleTwoWayTalk = { viewModel.toggleLiveSessionTwoWayTalk(context) },
                        onToggleSessionActive = { viewModel.toggleLiveSessionActive(context) },
                        onToggleCameraFacing = { viewModel.toggleLiveCameraFacing() },
                        onCaptureSnapshot = {
                            LiveMediaSessionManager.shareSessionSnapshot(
                                context = context,
                                childName = state.selectedChild.name,
                                mode = state.liveSessionMode,
                                auditCode = state.premiumConfig.liveSessionAuditCode
                            )
                        },
                        onPublishCameraFrame = { base64, w, h ->
                            viewModel.publishCapturedFrameToCloud(base64, w, h)
                        },
                        onNavigateToPremium = { viewModel.navigateTo(AnchorScreen.PREMIUM_FEATURES) },
                        onInstantUnlockTrial = {
                            viewModel.launchPlayBillingPurchase(context as? Activity, billingManager, "PREMIUM_ANNUAL")
                        }
                    )
                    AnchorScreen.ACCOUNT_SETTINGS_DIAGNOSTICS -> AccountSettingsDiagnosticsScreen(
                        state = state,
                        onRunSelfTest = { viewModel.runConnectionSelfTest() },
                        onTogglePreference = { viewModel.toggleSettingPreference(it) },
                        onExportSystemLog = { viewModel.exportSystemLogBundle(context) },
                        onNavigate = { viewModel.navigateTo(it) },
                        onSignOut = { viewModel.signOutGuardian(context) }
                    )
                }
            }
        }
    }
}
