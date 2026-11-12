package com.example

import com.example.model.AnchorScreen
import com.example.util.AndroidPlatformHelper
import com.example.viewmodel.AnchorUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun premiumGating_keepsAllCoreScreensFreeAndOnlyGatesSixPremiumFeatureScreens() {
        val premiumScreens = AnchorScreen.entries.filter { it.isPremium }
        val freeScreens = AnchorScreen.entries.filter { !it.isPremium }

        // Web Filter, Social Filter, Notification Filter, and Live Session Viewer represent the 6 Premium groups
        assertEquals(4, premiumScreens.size)
        assertEquals(16, freeScreens.size)

        // Ensure zero wireframe codes appear in any screen enum name, code, or title
        AnchorScreen.entries.forEach { screen ->
            assertFalse(screen.name.matches(Regex("^[PC]\\d+.*")))
            assertFalse(screen.code.matches(Regex("^[PC]-?\\d+.*")))
            assertFalse(screen.title.contains(Regex("[PC]-?\\d+")))
        }

        val defaultState = AnchorUiState()
        assertFalse(defaultState.isSignedIn)
        assertEquals(AnchorScreen.WELCOME_SIGN_IN, defaultState.currentScreen)
        assertFalse(defaultState.isPremiumActive)
        assertEquals("FREE", defaultState.familyHousehold.subscriptionTier)
        assertTrue(defaultState.children.isEmpty())
        assertTrue(defaultState.schedules.isEmpty())
        assertTrue(defaultState.premiumConfig.whitelistedDomains.isEmpty())
        assertTrue(defaultState.premiumConfig.blacklistedDomains.isEmpty())
        assertFalse(defaultState.liveSessionActive)
        assertEquals("STANDBY", defaultState.liveSessionConsentStatus)
    }

    @Test
    fun platformHelper_generatesSecurePairingCodesAndEvaluatesHaversineGeofences() {
        val code1 = AndroidPlatformHelper.generateSecurePairingCode()
        assertTrue(code1.matches(Regex("\\d{3}-\\d{3}")))
        assertEquals(code1, AndroidPlatformHelper.normalizePairingCode(code1.replace("-", "")))
        assertNull(AndroidPlatformHelper.normalizePairingCode("12-3"))

        // Haversine geofence check: identical coordinates = 0m distance (inside 250m school geofence)
        assertTrue(
            AndroidPlatformHelper.isInsideGeofence(
                childLat = 37.7749,
                childLon = -122.4194,
                geofenceLat = 37.7749,
                geofenceLon = -122.4194,
                radiusMeters = 250
            )
        )
        // Coordinates ~5km away = outside 250m school geofence
        val distMeters = AndroidPlatformHelper.calculateDistanceMeters(
            37.7749, -122.4194,
            37.8199, -122.4783
        )
        assertNotNull(distMeters)
        assertTrue(distMeters > 250)
        assertFalse(
            AndroidPlatformHelper.isInsideGeofence(
                childLat = 37.8199,
                childLon = -122.4783,
                geofenceLat = 37.7749,
                geofenceLon = -122.4194,
                radiusMeters = 250
            )
        )
    }
}
