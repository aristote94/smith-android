package fr.smith.android.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import fr.smith.android.R
import fr.smith.android.domain.model.HomeStatus
import fr.smith.android.ui.home.labelRes
import fr.smith.android.ui.theme.LocalSmithStatusColors
import fr.smith.android.ui.theme.SmithSpacing

/**
 * Pastille de statut : point coloré + libellé.
 *
 * La couleur ne porte jamais l'information seule — le libellé textuel la double, pour les
 * daltoniens comme pour TalkBack, qui lit une description unique au lieu de deux fragments.
 */
@Composable
fun StatusBadge(status: HomeStatus, modifier: Modifier = Modifier) {
    val color = LocalSmithStatusColors.current.forStatus(status)
    val label = stringResource(status.labelRes())
    val description = stringResource(R.string.cd_status, label)

    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SmithSpacing.sm),
    ) {
        Spacer(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = color,
        )
    }
}
