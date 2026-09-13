package fr.smith.android.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.smith.android.R
import fr.smith.android.ui.home.components.AppTile
import fr.smith.android.ui.home.components.HomeHeader
import fr.smith.android.ui.home.components.HouseCard
import fr.smith.android.ui.home.components.SectionTitle
import fr.smith.android.ui.theme.SmithSpacing
import java.time.ZoneId
import java.util.Locale
import kotlinx.coroutines.launch

/** Largeur minimale d'une tuile ; la grille en déduit le nombre de colonnes. */
private val TileMinWidth = 92.dp

/**
 * Écran d'accueil.
 *
 * Une grille adaptative unique porte l'en-tête, la carte maison et les tuiles : le nombre
 * de colonnes suit la largeur disponible, donc la mise en page tient du petit téléphone en
 * portrait à la tablette en paysage sans cas particulier.
 *
 * @param onLaunchApp renvoie `true` si l'application a bien été ouverte ; `false` déclenche
 *   un message expliquant pourquoi rien ne s'est passé.
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onLaunchApp: (AppTileState) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val locale = configuration.locales.get(0) ?: Locale.getDefault()
    val zone = remember { ZoneId.systemDefault() }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
        when (state) {
            HomeUiState.Loading -> CenteredMessage(
                text = stringResource(R.string.state_loading),
                modifier = Modifier.fillMaxSize(),
            )

            is HomeUiState.Error -> CenteredMessage(
                text = stringResource(R.string.state_error),
                modifier = Modifier.fillMaxSize(),
            )

            is HomeUiState.Ready -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(TileMinWidth),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = SmithSpacing.lg,
                        end = SmithSpacing.lg,
                        top = contentPadding.calculateTopPadding() + SmithSpacing.lg,
                        bottom = contentPadding.calculateBottomPadding() + SmithSpacing.xl,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(SmithSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(SmithSpacing.lg),
                ) {
                    item(key = "header", span = { GridItemSpan(maxLineSpan) }) {
                        HomeHeader(
                            observedAt = state.snapshot.observedAt,
                            zone = zone,
                            locale = locale,
                        )
                    }
                    item(key = "house", span = { GridItemSpan(maxLineSpan) }) {
                        HouseCard(snapshot = state.snapshot, locale = locale)
                    }
                    item(key = "apps-title", span = { GridItemSpan(maxLineSpan) }) {
                        SectionTitle(stringResource(R.string.home_section_apps))
                    }
                    items(
                        items = state.apps,
                        key = { it.entry.packageName },
                    ) { tile ->
                        AppTile(
                            state = tile,
                            onClick = {
                                if (!onLaunchApp(tile)) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            context.getString(
                                                R.string.toast_app_not_installed,
                                                tile.entry.label,
                                            ),
                                        )
                                    }
                                }
                            },
                        )
                    }
                    item(key = "footer", span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = stringResource(R.string.home_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CenteredMessage(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
