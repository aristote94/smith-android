package fr.smith.android.ui.home

import androidx.annotation.StringRes
import fr.smith.android.R
import fr.smith.android.domain.model.HomeStatus
import fr.smith.android.domain.model.RoomId

/**
 * Traduction des identifiants du domaine en ressources de chaînes.
 *
 * Les `when` sont exhaustifs et sans branche `else` : ajouter une valeur d'énumération
 * casse la compilation ici, au lieu d'afficher silencieusement un libellé faux.
 */
@StringRes
fun RoomId.labelRes(): Int = when (this) {
    RoomId.LIVING_ROOM -> R.string.room_living_room
    RoomId.BEDROOM -> R.string.room_bedroom
    RoomId.OFFICE -> R.string.room_office
}

@StringRes
fun HomeStatus.labelRes(): Int = when (this) {
    HomeStatus.NOMINAL -> R.string.status_nominal
    HomeStatus.ATTENTION -> R.string.status_attention
    HomeStatus.ALERT -> R.string.status_alert
}
