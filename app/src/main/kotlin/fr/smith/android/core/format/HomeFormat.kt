package fr.smith.android.core.format

import java.math.RoundingMode
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * Mise en forme des valeurs numériques et temporelles.
 *
 * Ne produit que des **nombres formatés** : unités, libellés et ponctuation viennent des
 * ressources de chaînes. C'est ce qui permet de traduire l'application sans toucher au code,
 * et de tester ce fichier sur JVM sans Android.
 *
 * Les instances [NumberFormat] et [DateTimeFormatter] ne sont pas thread-safe : elles sont
 * créées à chaque appel. Côté Compose, encapsuler les appels dans un `remember` si un profil
 * montre que ça compte — aucune mesure ne l'indique aujourd'hui.
 */
object HomeFormat {

    /** Arrondi explicite : le comportement par défaut de [NumberFormat] est HALF_EVEN. */
    private val ROUNDING = RoundingMode.HALF_UP

    fun decimal(value: Float, locale: Locale, fractionDigits: Int = 1): String =
        NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = fractionDigits
            maximumFractionDigits = fractionDigits
            roundingMode = ROUNDING
        }.format(value)

    fun integer(value: Int, locale: Locale): String =
        NumberFormat.getIntegerInstance(locale).format(value)

    /** Heure locale au format court de la locale (« 09:27 » en fr, « 9:27 AM » en en-US). */
    fun time(instant: Instant, zone: ZoneId, locale: Locale): String =
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            .withLocale(locale)
            .withZone(zone)
            .format(instant)

    /**
     * Date selon le motif fourni par les ressources (`format_date_pattern`), pour que
     * l'ordre des composants suive la langue et non une convention codée en dur.
     */
    fun date(instant: Instant, zone: ZoneId, pattern: String, locale: Locale): String =
        DateTimeFormatter.ofPattern(pattern, locale)
            .withZone(zone)
            .format(instant)
}
