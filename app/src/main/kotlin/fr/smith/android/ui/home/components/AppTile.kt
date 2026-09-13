package fr.smith.android.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.smith.android.R
import fr.smith.android.ui.home.AppTileState
import fr.smith.android.ui.theme.SmithShape
import fr.smith.android.ui.theme.SmithSpacing

/** Taille minimale d'une cible tactile recommandée par les règles d'accessibilité Android. */
private val MinTouchTarget = 48.dp

/**
 * Tuile d'application : l'icône du pack telle qu'elle apparaîtra sur l'écran d'accueil,
 * plus le nom de l'application.
 *
 * La tuile reste cliquable même quand l'application est absente : le retour est explicite
 * plutôt qu'un élément inerte dont on ne sait pas s'il est cassé.
 */
@Composable
fun AppTile(
    state: AppTileState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(
        if (state.installed) R.string.cd_app_tile else R.string.cd_app_tile_unavailable,
        state.entry.label,
    )

    Column(
        modifier = modifier
            .sizeIn(minWidth = MinTouchTarget, minHeight = MinTouchTarget)
            .clearAndSetSemantics {
                contentDescription = description
                onClick { onClick(); true }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SmithSpacing.sm),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(SmithShape.tile)),
            color = MaterialTheme.colorScheme.surface,
            onClick = onClick,
        ) {
            Image(
                painter = painterResource(state.entry.iconRes),
                contentDescription = null,
                modifier = Modifier.alpha(if (state.installed) 1f else DIMMED_ALPHA),
            )
        }
        Text(
            text = state.entry.label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (state.installed) 1f else DIMMED_ALPHA),
        )
    }
}

private const val DIMMED_ALPHA = 0.45f
