package fr.smith.android.core

import fr.smith.android.core.format.HomeFormat
import java.time.Instant
import java.time.ZoneOffset
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Les séparateurs décimaux et l'ordre des composants de date dépendent de la locale.
 * Ces tests verrouillent le comportement français, celui que le projet cible en premier.
 * L'espace insécable inséré par le JDK pour les milliers est normalisé avant comparaison.
 */
class HomeFormatTest {

    private val french = Locale.FRANCE

    @Test
    fun `un decimal francais utilise la virgule`() {
        assertEquals("21,4", HomeFormat.decimal(21.44f, french))
    }

    /**
     * L'arrondi est HALF_UP **sur la valeur binaire réellement stockée**, pas sur la
     * décimale écrite dans le code : `21.45f` vaut 21.450000762…, donc 21,5 ; `1.15f` vaut
     * 1.149999976…, donc 1,1. Ce n'est pas un défaut de mise en forme mais la représentation
     * des flottants. Si un jour une valeur doit être exacte au centime, le domaine devra
     * porter un `BigDecimal`, pas la couche de présentation.
     */
    @Test
    fun `l arrondi est au demi superieur sur la valeur binaire reelle`() {
        assertEquals("21,5", HomeFormat.decimal(21.45f, french))
        assertEquals("1,1", HomeFormat.decimal(1.15f, french))
        assertEquals("2,3", HomeFormat.decimal(2.25f, french))
    }

    @Test
    fun `le nombre de decimales est respecte meme sur un entier`() {
        assertEquals("9,0", HomeFormat.decimal(9f, french))
        assertEquals("9", HomeFormat.decimal(9f, french, fractionDigits = 0))
    }

    @Test
    fun `un decimal anglais utilise le point`() {
        assertEquals("21.4", HomeFormat.decimal(21.44f, Locale.US))
    }

    @Test
    fun `la date suit le motif fourni`() {
        val instant = Instant.parse("2026-04-16T09:27:00Z")
        assertEquals(
            "jeudi 16 avril",
            HomeFormat.date(instant, ZoneOffset.UTC, "EEEE d MMMM", french),
        )
    }

    @Test
    fun `l heure suit le fuseau demande`() {
        val instant = Instant.parse("2026-04-16T09:27:00Z")
        val utc = HomeFormat.time(instant, ZoneOffset.UTC, french)
        val paris = HomeFormat.time(instant, ZoneOffset.ofHours(2), french)
        assertEquals("09:27", utc)
        assertEquals("11:27", paris)
    }
}
