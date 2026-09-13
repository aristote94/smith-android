package fr.smith.android.domain.model

import java.time.Instant

/**
 * État instantané de la maison, tel qu'il est présenté à l'écran et dans le widget.
 *
 * Modèle volontairement immuable et sans dépendance Android : il est produit par la
 * couche data, consommé par l'UI et par le widget, et testable sur JVM.
 */
data class HomeSnapshot(
    val observedAt: Instant,
    val rooms: List<RoomClimate>,
    val energy: EnergyUsage,
    val cameras: CameraFleet,
) {
    /** Pièce mise en avant (première de la liste) ; `null` si aucune pièce n'est connue. */
    val primaryRoom: RoomClimate? = rooms.firstOrNull()

    /**
     * Statut global, dérivé des mesures — jamais stocké, donc jamais désynchronisé.
     *
     * Règles, de la plus grave à la plus bénigne :
     * - aucune caméra en ligne alors que le parc en déclare : alerte ;
     * - au moins une caméra hors ligne, ou une pièce hors plage de confort : attention ;
     * - sinon : nominal.
     */
    val status: HomeStatus = when {
        cameras.total > 0 && cameras.online == 0 -> HomeStatus.ALERT
        cameras.offline > 0 -> HomeStatus.ATTENTION
        rooms.any { it.temperatureCelsius !in COMFORT_RANGE } -> HomeStatus.ATTENTION
        else -> HomeStatus.NOMINAL
    }

    companion object {
        /** Plage de confort retenue pour le statut ; volontairement large. */
        val COMFORT_RANGE = 17.0f..25.0f
    }
}

/** Gravité du statut global. L'ordre de déclaration va du plus calme au plus grave. */
enum class HomeStatus { NOMINAL, ATTENTION, ALERT }

/**
 * Identifiant stable d'une pièce.
 *
 * Le domaine ne transporte jamais de libellé affichable : la traduction est faite par la
 * couche de présentation. Renommer une pièce à l'écran ne peut donc rien casser en amont.
 */
enum class RoomId { LIVING_ROOM, BEDROOM, OFFICE }

/**
 * Climat d'une pièce.
 *
 * @param humidityPercent humidité relative, bornée à 0..100 à la construction.
 */
data class RoomClimate(
    val id: RoomId,
    val temperatureCelsius: Float,
    val humidityPercent: Int,
) {
    init {
        require(humidityPercent in 0..100) { "Humidité hors bornes : $humidityPercent" }
        require(temperatureCelsius.isFinite()) { "Température non finie" }
    }
}

/** Consommation électrique : puissance instantanée et cumul du jour. */
data class EnergyUsage(
    val instantaneousKilowatts: Float,
    val dailyKilowattHours: Float,
) {
    init {
        require(instantaneousKilowatts >= 0f) { "Puissance négative" }
        require(dailyKilowattHours >= 0f) { "Énergie négative" }
    }
}

/** Parc de caméras. */
data class CameraFleet(
    val online: Int,
    val total: Int,
) {
    init {
        require(total >= 0) { "Parc négatif" }
        require(online in 0..total) { "Caméras en ligne hors bornes : $online/$total" }
    }

    val offline: Int = total - online
}
