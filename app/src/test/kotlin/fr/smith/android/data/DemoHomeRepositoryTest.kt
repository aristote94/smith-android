package fr.smith.android.data

import fr.smith.android.data.demo.DemoHomeRepository
import fr.smith.android.domain.model.HomeSnapshot
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoHomeRepositoryTest {

    @Test
    fun `deux lectures au meme instant donnent exactement le meme etat`() {
        val repository = DemoHomeRepository(fixedClock("2026-04-16T09:27:00Z"))
        val first = repository.snapshotAt(Instant.parse("2026-04-16T09:27:00Z"))
        val second = repository.snapshotAt(Instant.parse("2026-04-16T09:27:00Z"))
        assertEquals(first, second)
    }

    @Test
    fun `l etat varie au cours de la journee`() {
        val repository = DemoHomeRepository(fixedClock("2026-04-16T00:00:00Z"))
        val night = repository.snapshotAt(Instant.parse("2026-04-16T04:00:00Z"))
        val afternoon = repository.snapshotAt(Instant.parse("2026-04-16T16:00:00Z"))
        assertTrue(
            "La puissance de l'après-midi doit dépasser celle de la nuit",
            afternoon.energy.instantaneousKilowatts > night.energy.instantaneousKilowatts,
        )
    }

    @Test
    fun `toutes les valeurs restent dans des bornes plausibles sur 24 heures`() {
        val repository = DemoHomeRepository(fixedClock("2026-04-16T00:00:00Z"))
        val base = Instant.parse("2026-04-16T00:00:00Z")
        for (minute in 0 until 1440) {
            val snapshot = repository.snapshotAt(base.plusSeconds(minute * 60L))
            assertTrue(
                "Puissance hors bornes à $minute min : ${snapshot.energy.instantaneousKilowatts}",
                snapshot.energy.instantaneousKilowatts in 0f..5f,
            )
            snapshot.rooms.forEach { room ->
                assertTrue(
                    "Température hors bornes à $minute min : ${room.temperatureCelsius}",
                    room.temperatureCelsius in 10f..30f,
                )
                assertTrue(room.humidityPercent in 0..100)
            }
        }
    }

    @Test
    fun `les valeurs sont arrondies au dixieme`() {
        val repository = DemoHomeRepository(fixedClock("2026-04-16T09:27:00Z"))
        val snapshot = repository.snapshotAt(Instant.parse("2026-04-16T09:27:00Z"))
        assertRoundedToTenth(snapshot)
    }

    @Test
    fun `le flux emet immediatement puis a chaque intervalle`() = runTest {
        val repository = DemoHomeRepository(
            clock = fixedClock("2026-04-16T09:27:00Z"),
            refreshInterval = Duration.ofSeconds(30),
        )
        val emissions = repository.observeSnapshot().take(3).toList()
        assertEquals(3, emissions.size)
        // Horloge figée : les trois émissions sont identiques, ce qui prouve que la
        // variation vient bien de l'horloge et de rien d'autre.
        assertEquals(List(3) { emissions.first() }, emissions)
    }

    @Test
    fun `un intervalle nul est refuse`() {
        assertThrows(IllegalArgumentException::class.java) {
            DemoHomeRepository(refreshInterval = Duration.ZERO)
        }
    }

    private fun assertRoundedToTenth(snapshot: HomeSnapshot) {
        val values = snapshot.rooms.map { it.temperatureCelsius } +
            snapshot.energy.instantaneousKilowatts +
            snapshot.energy.dailyKilowattHours
        values.forEach { value ->
            val scaled = value * 10f
            assertEquals(
                "Valeur non arrondie au dixième : $value",
                scaled.toDouble(),
                Math.round(scaled).toDouble(),
                1e-3,
            )
        }
    }

    private fun fixedClock(instant: String): Clock =
        Clock.fixed(Instant.parse(instant), ZoneOffset.UTC)
}
