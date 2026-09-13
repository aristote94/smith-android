package fr.smith.android.di

import android.content.Context
import fr.smith.android.data.demo.DemoHomeRepository
import fr.smith.android.domain.repository.HomeRepository
import fr.smith.android.iconpack.InstalledAppsProvider
import fr.smith.android.iconpack.PackageManagerInstalledApps

/**
 * Graphe de dépendances de l'application.
 *
 * Injection manuelle assumée : le projet a deux dépendances à câbler et aucun besoin de
 * portées multiples. Un conteneur explicite évite un processeur d'annotations (Hilt/KSP),
 * donc des builds plus rapides et zéro magie au démarrage.
 * Le jour où le graphe grossit — plusieurs sources de données, portées écran, tests
 * instrumentés à isoler — remplacer cette interface par Hilt est mécanique : les appelants
 * ne connaissent que [AppContainer].
 */
interface AppContainer {
    val homeRepository: HomeRepository
    val installedApps: InstalledAppsProvider
}

/** Implémentation de production. Tout est paresseux : rien n'est construit au démarrage. */
class DefaultAppContainer(context: Context) : AppContainer {
    private val appContext = context.applicationContext

    override val homeRepository: HomeRepository by lazy { DemoHomeRepository() }
    override val installedApps: InstalledAppsProvider by lazy {
        PackageManagerInstalledApps(appContext)
    }
}

/** Implémenté par la classe `Application` ; permet de récupérer le graphe sans singleton global. */
interface AppContainerProvider {
    val appContainer: AppContainer
}

/**
 * Accès au graphe depuis n'importe quel [Context].
 *
 * Échoue bruyamment plutôt que silencieusement : un `Application` non déclaré dans le
 * manifeste est une erreur de configuration, pas un cas à contourner à l'exécution.
 */
val Context.appContainer: AppContainer
    get() = (applicationContext as? AppContainerProvider)?.appContainer
        ?: error(
            "L'Application n'implémente pas AppContainerProvider : " +
                "vérifier android:name dans AndroidManifest.xml",
        )
