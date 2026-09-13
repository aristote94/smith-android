package fr.smith.android.domain.repository

import fr.smith.android.domain.model.HomeSnapshot
import kotlinx.coroutines.flow.Flow

/**
 * Source des données domestiques.
 *
 * Une seule implémentation aujourd'hui ([fr.smith.android.data.demo.DemoHomeRepository]).
 * Le jour où Home Assistant arrive, il suffit d'une seconde implémentation branchée
 * dans [fr.smith.android.di.AppContainer] : ni l'UI ni le widget ne changent.
 */
interface HomeRepository {

    /**
     * Flux d'états. Émet immédiatement une première valeur, puis à chaque rafraîchissement.
     * Le flux est froid : il ne travaille que tant qu'un collecteur est actif.
     */
    fun observeSnapshot(): Flow<HomeSnapshot>

    /**
     * Valeur ponctuelle, pour les appelants qui ne peuvent pas collecter durablement
     * (rafraîchissement de widget déclenché par le système, par exemple).
     */
    suspend fun currentSnapshot(): HomeSnapshot
}
