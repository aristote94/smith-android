package fr.smith.android.ui.home

import fr.smith.android.domain.model.HomeSnapshot
import fr.smith.android.iconpack.IconPackEntry

/** État affichable de l'écran d'accueil. Exhaustif : l'UI n'a aucun cas implicite à gérer. */
sealed interface HomeUiState {

    /** Avant la première émission du dépôt. */
    data object Loading : HomeUiState

    data class Ready(
        val snapshot: HomeSnapshot,
        val apps: List<AppTileState>,
    ) : HomeUiState

    /** Le flux de données a échoué ; le message est journalisé, pas affiché brut. */
    data class Error(val message: String?) : HomeUiState
}

/**
 * Une tuile du pack.
 *
 * @param installed l'application correspondante est présente et lançable. Une tuile non
 *   installée reste affichée — c'est un aperçu du pack, pas un lanceur — mais elle est
 *   atténuée, et la toucher explique pourquoi rien ne s'ouvre.
 */
data class AppTileState(
    val entry: IconPackEntry,
    val installed: Boolean,
)
