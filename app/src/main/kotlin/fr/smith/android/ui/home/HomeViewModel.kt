package fr.smith.android.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.smith.android.di.AppContainer
import fr.smith.android.domain.model.HomeSnapshot
import fr.smith.android.domain.repository.HomeRepository
import fr.smith.android.iconpack.IconPackCatalog
import fr.smith.android.iconpack.IconPackEntry
import fr.smith.android.iconpack.InstalledAppsProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Assemble l'état de la maison et l'état d'installation des applications du pack.
 *
 * Ne connaît aucune API Android hors [Log] : testable sur JVM avec des doublures.
 */
class HomeViewModel(
    homeRepository: HomeRepository,
    private val installedApps: InstalledAppsProvider,
    private val catalog: List<IconPackEntry> = IconPackCatalog.entries,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val installedPackages = MutableStateFlow<Set<String>>(emptySet())

    val uiState: StateFlow<HomeUiState> =
        combine<HomeSnapshot, Set<String>, HomeUiState>(
            homeRepository.observeSnapshot(),
            installedPackages,
        ) { snapshot, packages ->
            HomeUiState.Ready(
                snapshot = snapshot,
                apps = catalog.map { AppTileState(it, it.packageName in packages) },
            )
        }
            .catch { throwable ->
                // Le flux de démonstration ne peut pas échouer aujourd'hui ; une source
                // réseau le pourra. On dégrade l'écran au lieu de laisser remonter.
                Log.e(TAG, "Flux de données interrompu", throwable)
                emit(HomeUiState.Error(throwable.message))
            }
            .stateIn(
                scope = viewModelScope,
                // Survit aux rotations sans relancer la collecte, s'arrête si l'écran part
                // réellement en arrière-plan.
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = HomeUiState.Loading,
            )

    /**
     * Recalcule quelles applications du catalogue sont installées.
     *
     * À rappeler à chaque retour au premier plan : l'utilisateur a pu installer ou
     * désinstaller une application pendant que l'écran était masqué.
     */
    fun refreshInstalledApps() {
        viewModelScope.launch {
            val packages = catalog.map { it.packageName }
            // PackageManager fait des appels IPC : jamais sur le thread principal.
            installedPackages.value = withContext(ioDispatcher) {
                runCatching { installedApps.installedPackages(packages) }
                    .onFailure { Log.w(TAG, "Interrogation du PackageManager échouée", it) }
                    .getOrDefault(emptySet())
            }
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(
                    homeRepository = container.homeRepository,
                    installedApps = container.installedApps,
                )
            }
        }
    }
}
