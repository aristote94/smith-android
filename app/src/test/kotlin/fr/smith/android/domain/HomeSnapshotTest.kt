package fr.smith.android.domain

import fr.smith.android.domain.model.CameraFleet
import fr.smith.android.domain.model.EnergyUsage
import fr.smith.android.domain.model.HomeSnapshot
import fr.smith.android.domain.model.HomeStatus
import fr.smith.android.domain.model.RoomClimate
import fr.smith.android.domain.model.RoomId
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class HomeSnapshotTest {

    @Test
    fun `statut nominal quand tout est en ligne et dans la plage de confort`() {
        assertEquals(HomeStatus.NOMINAL, snapshot().status)
    }

    @Test
    fun `une camera hors ligne fait basculer en attention`() {
        val result = snapshot(cameras = CameraFleet(online = 3, total = 4))
        assertEquals(HomeStatus.ATTENTION, result.status)
    }

    @Test
    fun `plus aucune camera en ligne declenche une alerte`() {
        val result = snapshot(cameras = CameraFleet(online = 0, total = 4))
        assertEquals(HomeStatus.ALERT, result.status)
    }

    @Test
    fun `un parc vide ne declenche pas d alerte`() {
        val result = snapshot(cameras = CameraFleet(online = 0, total = 0))
        assertEquals(HomeStatus.NOMINAL, result.status)
    }

    @Test
    fun `une temperature hors plage de confort declenche l attention`() {
        val result = snapshot(
            rooms = listOf(RoomClimate(RoomId.LIVING_ROOM, 27.0f, 42)),
        )
        assertEquals(HomeStatus.ATTENTION, result.status)
    }

    @Test
    fun `la piece principale est la premiere de la liste`() {
        val result = snapshot(
            rooms = listOf(
                RoomClimate(RoomId.OFFICE, 20f, 40),
                RoomClimate(RoomId.BEDROOM, 20f, 40),
            ),
        )
        assertEquals(RoomId.OFFICE, result.primaryRoom?.id)
    }

    @Test
    fun `une humidite hors bornes est refusee a la construction`() {
        assertThrows(IllegalArgumentException::class.java) {
            RoomClimate(RoomId.BEDROOM, 20f, 140)
        }
    }

    @Test
    fun `plus de cameras en ligne que de cameras est refuse`() {
        assertThrows(IllegalArgumentException::class.java) {
            CameraFleet(online = 5, total = 4)
        }
    }

    private fun snapshot(
        rooms: List<RoomClimate> = listOf(RoomClimate(RoomId.LIVING_ROOM, 21.0f, 42)),
        cameras: CameraFleet = CameraFleet(online = 4, total = 4),
    ) = HomeSnapshot(
        observedAt = Instant.EPOCH,
        rooms = rooms,
        energy = EnergyUsage(1.2f, 9.4f),
        cameras = cameras,
    )
}
