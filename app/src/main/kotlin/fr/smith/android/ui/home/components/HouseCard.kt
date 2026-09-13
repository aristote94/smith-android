package fr.smith.android.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import fr.smith.android.R
import fr.smith.android.core.format.HomeFormat
import fr.smith.android.domain.model.HomeSnapshot
import fr.smith.android.ui.home.labelRes
import fr.smith.android.ui.theme.SmithShape
import fr.smith.android.ui.theme.SmithSpacing
import java.util.Locale

/**
 * Carte « maison » : statut, pièce principale, énergie, caméras.
 *
 * Mise en page en [Row] de colonnes de poids égal plutôt qu'en positions absolues :
 * elle reste lisible du petit téléphone à la tablette en paysage.
 */
@Composable
fun HouseCard(
    snapshot: HomeSnapshot,
    locale: Locale,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SmithShape.card),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(SmithSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(SmithSpacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.home_section_house),
                    style = MaterialTheme.typography.titleMedium,
                )
                StatusBadge(status = snapshot.status)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SmithSpacing.md),
            ) {
                snapshot.primaryRoom?.let { room ->
                    Metric(
                        modifier = Modifier.weight(1f),
                        label = stringResource(room.id.labelRes()),
                        value = stringResource(
                            R.string.metric_temperature,
                            HomeFormat.decimal(room.temperatureCelsius, locale),
                        ),
                        caption = stringResource(
                            R.string.metric_humidity,
                            HomeFormat.integer(room.humidityPercent, locale),
                        ),
                    )
                }
                Metric(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.metric_power_label),
                    value = stringResource(
                        R.string.metric_power_value,
                        HomeFormat.decimal(snapshot.energy.instantaneousKilowatts, locale),
                    ),
                    caption = stringResource(
                        R.string.metric_energy_value,
                        HomeFormat.decimal(snapshot.energy.dailyKilowattHours, locale),
                    ),
                )
                Metric(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.metric_cameras_label),
                    value = HomeFormat.integer(snapshot.cameras.online, locale),
                    caption = stringResource(
                        R.string.metric_cameras_value,
                        HomeFormat.integer(snapshot.cameras.online, locale),
                        HomeFormat.integer(snapshot.cameras.total, locale),
                    ),
                )
            }
        }
    }
}

@Composable
private fun Metric(
    label: String,
    value: String,
    caption: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SmithSpacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Start,
        )
        Text(
            text = caption,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
