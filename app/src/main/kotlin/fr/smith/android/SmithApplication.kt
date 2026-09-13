package fr.smith.android

import android.app.Application
import fr.smith.android.di.AppContainer
import fr.smith.android.di.AppContainerProvider
import fr.smith.android.di.DefaultAppContainer

/** Point d'entrée du processus : ne fait qu'exposer le graphe de dépendances. */
class SmithApplication : Application(), AppContainerProvider {

    override val appContainer: AppContainer by lazy { DefaultAppContainer(this) }
}
