package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import com.example.model.AnchorScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun parentApp_authenticatesGuardianRoutesToOnboardingPairsDeviceNavigatesToDashboardAndUnlocksPremium() {
        // 1. Verify Welcome screen loads with Google Sign-In button and blocks unauthenticated navigation
        composeTestRule.onNodeWithTag("google_sign_in_button")
            .performScrollTo()
            .assertIsDisplayed()

        // Verify unauthenticated navigation guard blocks leaving Welcome screen
        composeTestRule.runOnIdle {
            composeTestRule.activity.parentViewModel.navigateTo(AnchorScreen.FAMILY_DASHBOARD)
            assertEquals(
                AnchorScreen.WELCOME_SIGN_IN,
                composeTestRule.activity.parentViewModel.uiState.value.currentScreen
            )
        }

        // 2. Authenticate Guardian session and verify automatic routing to Family Onboarding
        composeTestRule.runOnIdle {
            composeTestRule.activity.parentViewModel.onFirebaseUserAuthenticated(
                uid = "uid_guardian_test",
                displayName = "Nadia Rahman",
                email = "nadia@example.com"
            )
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Family Onboarding").assertIsDisplayed()
        composeTestRule.onNodeWithTag("onboarding_family_name_input")
            .performScrollTo()
            .performTextReplacement("The Rahman Family")
        composeTestRule.onNodeWithTag("onboarding_guardian_name_input")
            .performScrollTo()
            .performTextReplacement("Nadia Rahman")
        composeTestRule.onNodeWithTag("new_child_name_input")
            .performScrollTo()
            .performTextReplacement("Ayan")

        // Save household & child and proceed to Pairing screen
        composeTestRule.onNodeWithTag("continue_to_pair_code_btn")
            .performScrollTo()
            .performClick()

        // 3. Verify Add Child & Pair Device screen and test pairing code revocation & regeneration
        composeTestRule.onNodeWithText("Add Child & Pair Device").assertIsDisplayed()
        composeTestRule.onNodeWithTag("pairing_code_display")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("revoke_pairing_code_btn")
            .performScrollTo()
            .performClick()

        composeTestRule.onNodeWithTag("refresh_pairing_code_btn")
            .performScrollTo()
            .performClick()

        // 4. Proceed from Pairing Screen to Family Dashboard
        composeTestRule.onNodeWithTag("go_to_dashboard_from_pairing_btn")
            .performScrollTo()
            .performClick()

        composeTestRule.onNodeWithText("Family Dashboard").assertIsDisplayed()

        // 5. Navigate from Dashboard Command Center to Web & Content Filters (Premium)
        composeTestRule.onNodeWithTag("quick_link_web_filter_rules")
            .performScrollTo()
            .performClick()

        // 6. Verify Free tier shows the Premium Paywall Banner on Web Filter screen
        composeTestRule.onNodeWithTag("premium_paywall_banner")
            .performScrollTo()
            .assertIsDisplayed()

        // 7. Activate subscription via Billing/Entitlement flow and verify Premium becomes active
        composeTestRule.onNodeWithTag("instant_unlock_premium_trial_btn")
            .performScrollTo()
            .performClick()
        composeTestRule.onNodeWithText("PREMIUM").assertIsDisplayed()
    }
}
