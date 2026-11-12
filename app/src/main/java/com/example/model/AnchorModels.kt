package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

enum class AnchorScreen(val code: String, val title: String, val isPremium: Boolean = false) {
    WELCOME_SIGN_IN("welcome_sign_in", "Welcome & Sign In"),
    FAMILY_ONBOARDING("family_onboarding", "Family Onboarding"),
    ADD_CHILD_PAIR_CODE("add_child_pair_code", "Add Child & Pair Device"),
    FAMILY_DASHBOARD("family_dashboard", "Family Dashboard"),
    CHILD_PROFILE_DEVICE("child_profile_device", "Child Profile & Device Status"),
    SCREEN_TIME_ANALYTICS("screen_time_analytics", "Screen-Time Analytics"),
    APP_USAGE_LIMITS("app_usage_limits", "App Usage & Limits"),
    SCHEDULES_DOWNTIME("schedules_downtime", "Schedules & Downtime"),
    LOCATION_GEOFENCES("location_geofences", "Location & Geofences"),
    ALERTS_SOS_ACTIVITY("alerts_sos_activity", "Alerts & SOS Activity"),
    EXTRA_TIME_REQUESTS("extra_time_requests", "Extra Time & App Requests"),
    FAMILY_CHAT_TASKS("family_chat_tasks", "Family Chat & Shared Tasks"),
    GUARDIAN_ROLES_CONTACTS("guardian_roles_contacts", "Guardian Roles & Trusted Contacts"),
    PRIVACY_EXPORT_DELETION("privacy_export_deletion", "Privacy, Data Rights & Deletion"),
    PREMIUM_FEATURES("premium_features", "Premium Features & Subscription"),
    WEB_FILTER_RULES("web_filter_rules", "Web & Content Filters", isPremium = true),
    SOCIAL_CONTENT_FILTERS("social_content_filters", "Social App & Content Filters", isPremium = true),
    NOTIFICATION_FILTER("notification_filter", "Notification & Distraction Filter", isPremium = true),
    LIVE_SESSION_VIEWER("live_session_viewer", "Active Live Session Viewer", isPremium = true),
    ACCOUNT_SETTINGS_DIAGNOSTICS("account_settings_diagnostics", "Account Settings & Diagnostics")
}

data class UserProfile(
    @DocumentId var id: String = "",
    var uid: String = "",
    var displayName: String = "",
    var email: String = "",
    var role: String = "PRIMARY_GUARDIAN",
    var familyId: String = "",
    var notificationCadence: String = "ALL_EVENTS",
    @ServerTimestamp var createdAt: Timestamp? = null,
    @ServerTimestamp var updatedAt: Timestamp? = null
)

data class FamilyHousehold(
    @DocumentId var id: String = "",
    var familyId: String = "",
    var familyName: String = "",
    var ownerUid: String = "",
    var subscriptionTier: String = "FREE", // FREE, PREMIUM_ANNUAL, PREMIUM_MONTHLY
    var subscriptionActive: Boolean = false,
    var subscriptionProductId: String = "",
    var purchaseToken: String = "",
    var subscriptionUpdatedAtEpochMs: Long = 0L,
    var autoApproveLearningApps: Boolean = true,
    var fairPlayNoticeMinutes: Int = 5,
    @ServerTimestamp var createdAt: Timestamp? = null,
    @ServerTimestamp var updatedAt: Timestamp? = null
)

data class ChildProfile(
    @DocumentId var id: String = "",
    var childId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var pairedChildUid: String = "",
    var name: String = "",
    var age: Int = 10,
    var gradeLabel: String = "",
    var avatarInitials: String = "CH",
    var deviceName: String = "",
    var deviceModel: String = "",
    var osVersion: String = "Android 14+",
    var batteryLevel: Int = 100,
    var chargingStatus: String = "Awaiting Device Telemetry",
    var networkType: String = "Wi-Fi",
    var networkName: String = "Connected Network",
    var storageFreeGb: Double = 64.0,
    var syncStatus: String = "PENDING", // ENFORCED, PENDING, FAILED, UNSUPPORTED, QUEUED_OFFLINE, LOCAL_ONLY
    var lastSyncLabel: String = "Awaiting Child Device Pairing",
    var usedMinutesToday: Int = 0,
    var dailyAllowanceMinutes: Int = 120,
    var currentMode: String = "Standard Protection",
    var locationLabel: String = "Location Pending",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var knoxVerified: Boolean = false,
    var accessibilityEnabled: Boolean = false,
    var deviceAdminEnabled: Boolean = false,
    var policyPausedUntil: String = "",
    @ServerTimestamp var updatedAt: Timestamp? = null
)

data class PairingCode(
    @DocumentId var id: String = "",
    var codeId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var childId: String = "",
    var childName: String = "",
    var code: String = "000-000",
    var expiresAtEpochMs: Long = 0L,
    var status: String = "ACTIVE", // ACTIVE, CONSUMED, EXPIRED, REVOKED
    var revoked: Boolean = false,
    var attemptCount: Int = 0,
    var maxAttempts: Int = 5,
    var consumedByChildUid: String = "",
    var consumedAtEpochMs: Long = 0L,
    var revokedAtEpochMs: Long = 0L,
    @ServerTimestamp var createdAt: Timestamp? = null
)

data class PairedDevice(
    @DocumentId var id: String = "",
    var deviceId: String = "",
    var childUid: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var childId: String = "",
    var childName: String = "",
    var codeId: String = "",
    var redeemedCode: String = "",
    var deviceName: String = "",
    var deviceModel: String = "",
    var osVersion: String = "",
    var status: String = "PAIRED", // PAIRED, REVOKED
    var pairedAtEpochMs: Long = 0L,
    var lastHeartbeatEpochMs: Long = 0L,
    @ServerTimestamp var updatedAt: Timestamp? = null
)

data class AppRule(
    @DocumentId var id: String = "",
    var ruleId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var childId: String = "",
    var packageName: String = "",
    var appName: String = "",
    var category: String = "Games & Entertainment",
    var dailyLimitMinutes: Int = 45,
    var usedMinutesToday: Int = 0,
    var status: String = "ENFORCED", // ENFORCED, PENDING, SCHEDULE_ONLY, WHITELISTED, BLOCKED
    var blocked: Boolean = false,
    var whitelisted: Boolean = false,
    var scheduleOnly: Boolean = false,
    @ServerTimestamp var updatedAt: Timestamp? = null
)

data class ScheduleRoutine(
    @DocumentId var id: String = "",
    var scheduleId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var childId: String = "",
    var title: String = "",
    var windowDescription: String = "",
    var daysSummary: String = "Sun, Mon, Tue, Wed, Thu",
    var ruleSummary: String = "Phone calls, Emergency SOS & Alarm Clock only",
    var iconType: String = "BEDTIME", // BEDTIME, SCHOOL, DINNER, CUSTOM
    var enabled: Boolean = true,
    var syncStatus: String = "ENFORCED",
    @ServerTimestamp var updatedAt: Timestamp? = null
)

data class SafeGeofence(
    @DocumentId var id: String = "",
    var geofenceId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var childId: String = "",
    var name: String = "",
    var radiusMeters: Int = 250,
    var triggerSummary: String = "Notify on Arrival & Departure",
    var scheduleSummary: String = "24/7 Active Boundary",
    var activeInside: Boolean = false,
    var alertsEnabled: Boolean = true,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    @ServerTimestamp var updatedAt: Timestamp? = null
)

data class SafetyAlert(
    @DocumentId var id: String = "",
    var alertId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var childId: String = "",
    var childName: String = "",
    var title: String = "",
    var description: String = "",
    var category: String = "GEOFENCE", // SOS, GEOFENCE, DEVICE_HEALTH, SYSTEM
    var severity: String = "INFO", // INFO, WARNING, CRITICAL
    var timestampLabel: String = "Just now",
    var acknowledged: Boolean = true,
    @ServerTimestamp var createdAt: Timestamp? = null
)

data class ChildRequest(
    @DocumentId var id: String = "",
    var requestId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var childId: String = "",
    var childName: String = "",
    var requestType: String = "EXTRA_TIME", // EXTRA_TIME, NEW_APP
    var targetName: String = "",
    var targetCategory: String = "Games & Entertainment",
    var requestedBonusMinutes: Int = 15,
    var childNote: String = "",
    var usedTodaySummary: String = "Daily cap reached",
    var status: String = "PENDING", // PENDING, APPROVED, DECLINED
    var decisionSummary: String = "",
    var decidedBy: String = "",
    @ServerTimestamp var createdAt: Timestamp? = null,
    @ServerTimestamp var updatedAt: Timestamp? = null
)

data class FamilyMessage(
    @DocumentId var id: String = "",
    var messageId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var childId: String = "",
    var senderName: String = "",
    var senderRole: String = "PARENT", // PARENT, CHILD, SYSTEM
    var text: String = "",
    @get:PropertyName("isSystemEvent") @set:PropertyName("isSystemEvent")
    var isSystemEvent: Boolean = false,
    var timestampLabel: String = "Just now",
    @ServerTimestamp var createdAt: Timestamp? = null
)

data class FamilyTask(
    @DocumentId var id: String = "",
    var taskId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var childId: String = "",
    var title: String = "",
    var rewardSummary: String = "+15m Bonus Screen Time",
    var dueSummary: String = "Due Today",
    var status: String = "IN_PROGRESS", // IN_PROGRESS, READY_FOR_REVIEW, COMPLETED
    var completedByChild: Boolean = false,
    var approvedByParent: Boolean = false,
    @ServerTimestamp var updatedAt: Timestamp? = null
)

data class GuardianContact(
    @DocumentId var id: String = "",
    var guardianId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var name: String = "",
    var relationRole: String = "",
    var emailOrPhone: String = "",
    @get:PropertyName("isPrimaryGuardian") @set:PropertyName("isPrimaryGuardian")
    var isPrimaryGuardian: Boolean = true,
    var permissionsSummary: String = "Full Policy & SOS Authority",
    var receiveGeofenceAlerts: Boolean = true,
    var receiveSosSms: Boolean = true,
    var status: String = "Verified Active",
    @ServerTimestamp var updatedAt: Timestamp? = null
)

data class PremiumFilterConfig(
    @DocumentId var id: String = "",
    var configId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var childId: String = "",
    var webFilterProfile: String = "BALANCED_GUARDIAN", // STRICT_SAFEGUARD, BALANCED_GUARDIAN, CUSTOM_GRANULAR
    var blockedCategories: List<String> = emptyList(),
    var whitelistedDomains: List<String> = emptyList(),
    var blacklistedDomains: List<String> = emptyList(),
    var socialShieldProfile: String = "SUPERVISED", // SUPERVISED, STRICT_BLOCK, GRANULAR
    var discordDmWhitelistOnly: Boolean = true,
    var robloxChatFiltered: Boolean = true,
    var whatsappUnknownBlocked: Boolean = true,
    var youtubeRestrictedMode: Boolean = true,
    var notificationFilterEnabled: Boolean = true,
    var notificationProtocol: String = "BATCHING", // BATCHING, TOTAL_SILENCE, EMERGENCY_ONLY
    var batchMuteSocialDuringStudy: Boolean = true,
    var blockGamingStreakPrompts: Boolean = true,
    var allowLearningNotifications: Boolean = true,
    var liveSessionAuditCode: String = "#LIV-84920",
    @ServerTimestamp var updatedAt: Timestamp? = null
)

data class LiveSessionRequest(
    @DocumentId var id: String = "",
    var sessionId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var childId: String = "",
    var auditCode: String = "#LIV-84920",
    var mode: String = "Screen View", // Screen View, Front Camera, Ambient Audio
    var reason: String = "Safety & homework check-in",
    var status: String = "AWAITING_CHILD_CONSENT", // AWAITING_CHILD_CONSENT, CONSENT_GRANTED, DECLINED_BY_CHILD, ENDED
    var consentGranted: Boolean = false,
    var requestedAtEpochMs: Long = 0L,
    var sdpOffer: String = "",
    var sdpAnswer: String = "",
    var latestFrameBase64: String = "",
    var frameTimestampEpochMs: Long = 0L,
    var videoWidth: Int = 640,
    var videoHeight: Int = 480,
    var cameraFacing: String = "FRONT", // FRONT, BACK
    var audioLevelDb: Float = -45f,
    var latestAudioChunkBase64: String = "",
    var parentMicActive: Boolean = false,
    @ServerTimestamp var updatedAt: Timestamp? = null
)

data class WebRtcIceCandidate(
    @DocumentId var id: String = "",
    var candidateId: String = "",
    var sessionId: String = "",
    var familyId: String = "",
    var ownerUid: String = "",
    var senderRole: String = "PARENT", // PARENT, CHILD
    var sdpMid: String = "0",
    var sdpMLineIndex: Int = 0,
    var candidate: String = "",
    var createdAtEpochMs: Long = 0L,
    @ServerTimestamp var createdAt: Timestamp? = null
)
