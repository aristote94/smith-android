package fr.smith.android.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import fr.smith.android.R
import fr.smith.android.core.format.HomeFormat
import fr.smith.android.ui.theme.SmithSpacing
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

/** En-tête : marque, heure et date, dérivées de l'instant du dernier relevé. */
@Composable
fun HomeHeader(
    observedAt: Instant,
    zone: ZoneId,
    locale: Locale,
    modifier: Modifier = Modifier,
) {
    val datePattern = stringResource(R.string.format_date_pattern)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SmithSpacing.xs),
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = HomeFormat.time(observedAt, zone, locale),
            style = MaterialTheme.typography.displaySmall,
        )
        Text(
            text = HomeFormat.date(observedAt, zone, datePattern, locale),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
        )
    }
}

/** Intertitre de section, aligné sur la typographie des cartes. */
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.fillMaxWidth(),
    )
}
