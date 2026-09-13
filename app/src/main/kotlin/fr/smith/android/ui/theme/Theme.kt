package fr.smith.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import fr.smith.android.R
import fr.smith.android.domain.model.HomeStatus

/**
 * Couleurs sémantiques absentes du schéma Material 3.
 *
 * Passer par un CompositionLocal plutôt que par des constantes globales : un futur thème
 * clair, ou un thème par pièce, se branche sans toucher aux composants.
 */
@Immutable
data class SmithStatusColors(
    val nominal: Color,
    val attention: Color,
    val alert: Color,
) {
    fun forStatus(status: HomeStatus): Color = when (status) {
        HomeStatus.NOMINAL -> nominal
        HomeStatus.ATTENTION -> attention
        HomeStatus.ALERT -> alert
    }
}

val LocalSmithStatusColors = staticCompositionLocalOf {
    SmithStatusColors(Color.Unspecified, Color.Unspecified, Color.Unspecified)
}

/** Espacements. Une échelle unique évite les 12dp/13dp/14dp qui s'accumulent. */
object SmithSpacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
}

/** Rayons d'arrondi, alignés sur ceux des icônes du pack. */
object SmithShape {
    val card = 20.dp
    val tile = 18.dp
}

/**
 * Thème SMITH. Sombre uniquement : c'est un parti pris de design, pas un oubli — l'identité
 * du pack d'icônes repose sur le fond anthracite. Les couleurs proviennent de `colors.xml`,
 * partagé avec le widget et le thème de fenêtre, pour n'avoir qu'une seule palette.
 */
@Composable
fun SmithTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = colorResource(R.color.smith_steel),
        onPrimary = colorResource(R.color.smith_background),
        background = colorResource(R.color.smith_background),
        onBackground = colorResource(R.color.smith_text),
        surface = colorResource(R.color.smith_surface),
        onSurface = colorResource(R.color.smith_text),
        surfaceVariant = colorResource(R.color.smith_surface_variant),
        onSurfaceVariant = colorResource(R.color.smith_muted),
        outline = colorResource(R.color.smith_outline),
        error = colorResource(R.color.smith_alert),
    )
    val statusColors = SmithStatusColors(
        nominal = colorResource(R.color.smith_ok),
        attention = colorResource(R.color.smith_attention),
        alert = colorResource(R.color.smith_alert),
    )

    androidx.compose.runtime.CompositionLocalProvider(
        LocalSmithStatusColors provides statusColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SmithTypography,
            content = content,
        )
    }
}

private val SmithTypography = Typography().run {
    copy(
        displayMedium = displayMedium.copy(fontFamily = FontFamily.SansSerif),
        titleLarge = titleLarge.copy(fontFamily = FontFamily.SansSerif),
        bodyMedium = bodyMedium.copy(fontFamily = FontFamily.SansSerif),
        labelSmall = labelSmall.copy(fontFamily = FontFamily.SansSerif),
    )
}
