package fr.smith.android.ui

import fr.smith.android.domain.model.CameraFleet
import fr.smith.android.domain.model.EnergyUsage
import fr.smith.android.domain.model.HomeSnapshot
import fr.smith.android.domain.model.RoomClimate
import fr.smith.android.domain.model.RoomId
import fr.smith.android.domain.repository.HomeRepository
import fr.smith.android.iconpack.IconPackEntry
import fr.smith.android.iconpack.InstalledAppsProvider
import fr.smith.android.ui.home.HomeUiState
import fr.smith.android.ui.home.HomeViewModel
import java.io.IOException
import java.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `l etat initial est le chargement`() = runTest(StandardTestDispatcher()) {
        val viewModel = viewModel(FakeRepository(MutableStateFlow(snapshot())))
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `l etat devient pret des la premiere emission`() = runTest(dispatcher) {
        val viewModel = viewModel(FakeRepository(MutableStateFlow(snapshot())))
        val state = viewModel.uiState.first { it is HomeUiState.Ready } as HomeUiState.Ready
        assertEquals(snapshot(), state.snapshot)
        assertEquals(CATALOG.size, state.apps.size)
    }

    @Test
    fun `toutes les tuiles sont marquees absentes tant que rien n est rafraichi`() =
        runTest(dispatcher) {
            val viewModel = viewModel(FakeRepository(MutableStateFlow(snapshot())))
            val state = viewModel.uiState.first { it is HomeUiState.Ready } as HomeUiState.Ready
            assertTrue(state.apps.none { it.installed })
        }

    @Test
    fun `le rafraichissement marque les applications installees`() = runTest(dispatcher) {
        val viewModel = viewModel(
            repository = FakeRepository(MutableStateFlow(snapshot())),
            installedApps = { candidates -> candidates.take(1).toSet() },
        )
        viewModel.refreshInstalledApps()

        val state = viewModel.uiState.first {
            it is HomeUiState.Ready && it.apps.any(::installed)
        } as HomeUiState.Ready
        assertEquals(listOf(true, false), state.apps.map { it.installed })
    }

    @Test
    fun `un PackageManager en echec ne fait pas tomber l ecran`() = runTest(dispatcher) {
        val viewModel = viewModel(
            repository = FakeRepository(MutableStateFlow(snapshot())),
            installedApps = { error("PackageManager indisponible") },
        )
        viewModel.refreshInstalledApps()

        val state = viewModel.uiState.first { it is HomeUiState.Ready } as HomeUiState.Ready
        assertTrue(state.apps.none { it.installed })
    }

    @Test
    fun `un flux en erreur degrade l etat au lieu de propager`() = runTest(dispatcher) {
        val failing = object : HomeRepository {
            override fun observeSnapshot(): Flow<HomeSnapshot> = flow { throw IOException("hs") }
            override suspend fun currentSnapshot(): HomeSnapshot = throw IOException("hs")
        }
        val viewModel = viewModel(failing)
        val state = viewModel.uiState.first { it is HomeUiState.Error } as HomeUiState.Error
        assertEquals("hs", state.message)
    }

    private fun installed(tile: fr.smith.android.ui.home.AppTileState) = tile.installed

    private fun viewModel(
        repository: HomeRepository,
        installedApps: InstalledAppsProvider = InstalledAppsProvider { emptySet() },
    ) = HomeViewModel(
        homeRepository = repository,
        installedApps = installedApps,
        catalog = CATALOG,
        ioDispatcher = dispatcher,
    )

    private class FakeRepository(private val source: Flow<HomeSnapshot>) : HomeRepository {
        override fun observeSnapshot(): Flow<HomeSnapshot> = source
        override suspend fun currentSnapshot(): HomeSnapshot = source.first()
    }

    private companion object {
        val CATALOG = listOf(
            IconPackEntry("Alpha", "fr.test.alpha", "fr.test.alpha.Main", 1),
            IconPackEntry("Beta", "fr.test.beta", "fr.test.beta.Main", 2),
        )

        fun snapshot() = HomeSnapshot(
            observedAt = Instant.EPOCH,
            rooms = listOf(RoomClimate(RoomId.LIVING_ROOM, 21.0f, 42)),
            energy = EnergyUsage(1.2f, 9.4f),
            cameras = CameraFleet(online = 4, total = 4),
        )
    }
}
