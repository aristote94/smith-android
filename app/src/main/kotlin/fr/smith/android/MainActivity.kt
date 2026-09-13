package fr.smith.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.smith.android.di.appContainer
import fr.smith.android.iconpack.AppLauncher
import fr.smith.android.ui.home.HomeScreen
import fr.smith.android.ui.home.HomeViewModel
import fr.smith.android.ui.theme.SmithTheme

/**
 * Unique activité de l'application.
 *
 * Elle sert aussi de point d'entrée « thème » pour les launchers tiers : les filtres
 * d'intention correspondants sont déclarés dans le manifeste et ne doivent pas être retirés
 * sans casser l'installation du pack d'icônes.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val container = appContainer

        setContent {
            SmithTheme {
                val viewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.factory(container),
                )
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                val launcher = remember { AppLauncher(this) }

                // L'utilisateur a pu installer ou supprimer une application pendant que
                // l'écran était en arrière-plan : on revérifie à chaque retour au premier plan.
                LifecycleResumeEffect(viewModel) {
                    viewModel.refreshInstalledApps()
                    onPauseOrDispose { }
                }

                HomeScreen(
                    state = state,
                    onLaunchApp = { tile -> launcher.launch(tile.entry.packageName) },
                )
            }
        }
    }
}
