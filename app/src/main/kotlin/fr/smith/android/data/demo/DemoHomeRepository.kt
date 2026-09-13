package fr.smith.android.data.demo

import fr.smith.android.domain.model.CameraFleet
import fr.smith.android.domain.model.EnergyUsage
import fr.smith.android.domain.model.HomeSnapshot
import fr.smith.android.domain.model.RoomClimate
import fr.smith.android.domain.model.RoomId
import fr.smith.android.domain.repository.HomeRepository
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

/**
 * Données de démonstration, **entièrement déterministes** : la sortie ne dépend que de
 * l'horloge injectée. Aucune source d'aléa, donc un test peut figer l'horloge et vérifier
 * une valeur exacte.
 *
 * Aucun accès réseau, disque ou capteur : l'application reste hors-ligne par construction.
 *
 * @param clock horloge de référence ; [Clock.fixed] en test.
 * @param refreshInterval période entre deux émissions de [observeSnapshot].
 */
class DemoHomeRepository(
    private val clock: Clock = Clock.systemDefaultZone(),
    private val refreshInterval: Duration = DEFAULT_REFRESH_INTERVAL,
) : HomeRepository {

    init {
        require(!refreshInterval.isZero && !refreshInterval.isNegative) {
            "L'intervalle de rafraîchissement doit être strictement positif"
        }
    }

    override fun observeSnapshot(): Flow<HomeSnapshot> = flow {
        while (currentCoroutineContext().isActive) {
            emit(currentSnapshot())
            delay(refreshInterval.toMillis())
        }
    }

    override suspend fun currentSnapshot(): HomeSnapshot = snapshotAt(clock.instant())

    /** Exposé pour les tests et les aperçus Compose : pur, sans coroutine. */
    fun snapshotAt(instant: Instant): HomeSnapshot {
        val local = LocalDateTime.ofInstant(instant, clock.zone)
        val minuteOfDay = local.hour * MINUTES_PER_HOUR + local.minute

        // Cycle journalier : creux la nuit, pic en fin d'après-midi. Le déphasage
        // décale le maximum vers 16 h plutôt que midi.
        val dayCycle = sin((minuteOfDay - PHASE_OFFSET_MINUTES) / MINUTES_PER_DAY * TWO_PI)

        return HomeSnapshot(
            observedAt = instant,
            rooms = listOf(
                room(RoomId.LIVING_ROOM, 21.0f, 1.4f, 42, dayCycle),
                room(RoomId.BEDROOM, 19.4f, 1.0f, 45, dayCycle),
                room(RoomId.OFFICE, 20.6f, 1.2f, 40, dayCycle),
            ),
            energy = EnergyUsage(
                instantaneousKilowatts = round1(BASE_POWER_KW + POWER_AMPLITUDE_KW * dayCycle),
                dailyKilowattHours = round1(DAILY_ENERGY_KWH * (minuteOfDay / MINUTES_PER_DAY)),
            ),
            cameras = CameraFleet(online = CAMERA_COUNT, total = CAMERA_COUNT),
        )
    }

    private fun room(
        id: RoomId,
        baseTemperature: Float,
        amplitude: Float,
        baseHumidity: Int,
        dayCycle: Double,
    ) = RoomClimate(
        id = id,
        temperatureCelsius = round1(baseTemperature + amplitude * dayCycle),
        humidityPercent = (baseHumidity - HUMIDITY_AMPLITUDE * dayCycle).roundToInt().coerceIn(0, 100),
    )

    private fun round1(value: Double): Float = (value * 10.0).roundToInt() / 10.0f

    companion object {
        val DEFAULT_REFRESH_INTERVAL: Duration = Duration.ofSeconds(30)

        private const val MINUTES_PER_HOUR = 60
        private const val MINUTES_PER_DAY = 1440.0
        private const val TWO_PI = 2 * PI

        /** Décale le maximum du cycle vers 16 h (960 min) : sin est maximal à phase + 360 min. */
        private const val PHASE_OFFSET_MINUTES = 600.0

        private const val BASE_POWER_KW = 1.1
        private const val POWER_AMPLITUDE_KW = 0.6
        private const val DAILY_ENERGY_KWH = 9.4
        private const val HUMIDITY_AMPLITUDE = 4.0
        private const val CAMERA_COUNT = 4
    }
}
