package fr.smith.android.widget

import android.content.Context
import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import fr.smith.android.MainActivity
import fr.smith.android.R
import fr.smith.android.core.format.HomeFormat
import fr.smith.android.di.appContainer
import fr.smith.android.domain.model.HomeSnapshot
import fr.smith.android.domain.model.HomeStatus
import fr.smith.android.ui.home.labelRes
import java.time.ZoneId
import java.util.Locale

/** Récepteur déclaré au manifeste ; ne fait qu'associer le widget Glance au système. */
class SmithWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SmithWidget()
}

/**
 * Widget d'accueil.
 *
 * Il consomme exactement le même [fr.smith.android.domain.repository.HomeRepository] que
 * l'écran principal : une seule définition des données, donc aucun risque de voir deux
 * chiffres différents pour la même mesure.
 */
class SmithWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(setOf(CompactSize, ExpandedSize))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = context.appContainer.homeRepository

        provideContent {
            val snapshot by repository.observeSnapshot().collectAsState(initial = null)
            WidgetContent(snapshot)
        }
    }

    private companion object {
        val CompactSize = DpSize(180.dp, 70.dp)
        val ExpandedSize = DpSize(250.dp, 110.dp)
    }
}

@Composable
private fun WidgetContent(snapshot: HomeSnapshot?) {
    val context = LocalContext.current
    val compact = LocalSize.current.height < 100.dp
    val ink = smithColor(R.color.smith_text)
    val muted = smithColor(R.color.smith_muted)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(R.drawable.widget_background))
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .clickable(actionStartActivity<MainActivity>()),
        verticalAlignment = Alignment.Vertical.CenterVertically,
    ) {
        if (snapshot == null) {
            Text(
                text = context.getString(R.string.state_loading),
                style = TextStyle(color = muted, fontSize = 14.sp),
            )
            return@Column
        }

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            Text(
                text = context.getString(R.string.home_section_house),
                style = TextStyle(
                    color = ink,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = GlanceModifier.defaultWeight(),
            )
            Text(
                text = context.getString(snapshot.status.labelRes()),
                style = TextStyle(color = statusColor(snapshot.status), fontSize = 13.sp),
            )
        }

        Spacer(GlanceModifier.height(10.dp))

        val locale = context.resources.configuration.locales.get(0) ?: Locale.getDefault()
        val zone = ZoneId.systemDefault()
        val room = snapshot.primaryRoom

        if (room != null) {
            Text(
                text = context.getString(
                    R.string.metric_temperature,
                    HomeFormat.decimal(room.temperatureCelsius, locale),
                ) + "  ·  " + context.getString(
                    R.string.metric_humidity,
                    HomeFormat.integer(room.humidityPercent, locale),
                ),
                style = TextStyle(color = ink, fontSize = 15.sp),
            )
        }

        if (!compact) {
            Spacer(GlanceModifier.height(6.dp))
            Text(
                text = context.getString(
                    R.string.metric_power_value,
                    HomeFormat.decimal(snapshot.energy.instantaneousKilowatts, locale),
                ) + "  ·  " + context.getString(
                    R.string.metric_cameras_value,
                    HomeFormat.integer(snapshot.cameras.online, locale),
                    HomeFormat.integer(snapshot.cameras.total, locale),
                ),
                style = TextStyle(color = muted, fontSize = 13.sp),
            )
            Spacer(GlanceModifier.height(6.dp))
            Text(
                text = HomeFormat.time(snapshot.observedAt, zone, locale),
                style = TextStyle(color = muted, fontSize = 12.sp),
            )
        }
    }
}

/**
 * Couleur du widget lue depuis `colors.xml`.
 *
 * Glance n'expose publiquement que `ColorProvider(Color)` — la surcharge prenant un
 * identifiant de ressource est interne à la bibliothèque. On résout donc la ressource
 * nous-mêmes, ce qui évite de recopier la palette et garde `colors.xml` comme source unique.
 */
@Composable
private fun smithColor(@ColorRes id: Int): ColorProvider =
    ColorProvider(Color(LocalContext.current.getColor(id)))

@Composable
private fun statusColor(status: HomeStatus): ColorProvider = smithColor(
    when (status) {
        HomeStatus.NOMINAL -> R.color.smith_ok
        HomeStatus.ATTENTION -> R.color.smith_attention
        HomeStatus.ALERT -> R.color.smith_alert
    },
)
