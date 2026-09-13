package fr.smith.android.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import fr.smith.android.domain.model.CameraFleet
import fr.smith.android.domain.model.EnergyUsage
import fr.smith.android.domain.model.HomeSnapshot
import fr.smith.android.domain.model.RoomClimate
import fr.smith.android.domain.model.RoomId
import fr.smith.android.iconpack.IconPackCatalog
import fr.smith.android.ui.home.AppTileState
import fr.smith.android.ui.home.HomeScreen
import fr.smith.android.ui.home.HomeUiState
import fr.smith.android.ui.theme.SmithTheme
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Tests d'interface sur appareil ou émulateur.
 *
 * Ils ne tournent pas en intégration continue (pas d'émulateur dans le pipeline) mais sont
 * compilés à chaque build via `assembleDebugAndroidTest` : une signature cassée est donc
 * détectée en CI même sans exécution.
 */
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun etatDeChargement_afficheUnMessage() {
        composeRule.setContent {
            SmithTheme { HomeScreen(state = HomeUiState.Loading, onLaunchApp = { true }) }
        }
        composeRule.onNodeWithText("Chargement…", substring = true).assertIsDisplayed()
    }

    @Test
    fun etatPret_afficheLeStatutEtLesTuiles() {
        composeRule.setContent {
            SmithTheme { HomeScreen(state = readyState(), onLaunchApp = { true }) }
        }
        val first = IconPackCatalog.entries.first()
        composeRule.onNodeWithContentDescription(first.label, substring = true).assertIsDisplayed()
    }

    @Test
    fun clicSurUneTuile_declencheLeLancement() {
        val launched = mutableListOf<String>()
        composeRule.setContent {
            SmithTheme {
                HomeScreen(
                    state = readyState(),
                    onLaunchApp = { tile -> launched += tile.entry.packageName; true },
                )
            }
        }
        val first = IconPackCatalog.entries.first()
        composeRule.onNodeWithContentDescription(first.label, substring = true).performClick()
        assertEquals(listOf(first.packageName), launched)
    }

    private fun readyState() = HomeUiState.Ready(
        snapshot = HomeSnapshot(
            observedAt = Instant.parse("2026-04-16T09:27:00Z"),
            rooms = listOf(RoomClimate(RoomId.LIVING_ROOM, 21.4f, 42)),
            energy = EnergyUsage(1.2f, 9.4f),
            cameras = CameraFleet(online = 4, total = 4),
        ),
        apps = IconPackCatalog.entries.map { AppTileState(it, installed = true) },
    )
}
