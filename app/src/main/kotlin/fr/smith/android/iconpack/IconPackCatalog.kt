package fr.smith.android.iconpack

import androidx.annotation.DrawableRes
import fr.smith.android.R

/**
 * Généré par tools/gen_iconpack.py depuis tools/iconpack.json — ne pas éditer.
 *
 * Entrée du pack : une icône SMITH et l'application qu'elle habille.
 */
data class IconPackEntry(
    val label: String,
    val packageName: String,
    val activityName: String,
    @DrawableRes val iconRes: Int,
) {
    /** Forme attendue par `appfilter.xml`, utilisée pour vérifier la cohérence en test. */
    val component: String get() = "ComponentInfo{$packageName/$activityName}"
}

/** Catalogue complet du pack, dans l'ordre d'affichage. */
object IconPackCatalog {
    val entries: List<IconPackEntry> = listOf(
        IconPackEntry(
            label = "Phone",
            packageName = "com.google.android.dialer",
            activityName = "com.google.android.dialer.extensions.GoogleDialtactsActivity",
            iconRes = R.drawable.ic_phone,
        ),
        IconPackEntry(
            label = "Messages",
            packageName = "com.google.android.apps.messaging",
            activityName = "com.google.android.apps.messaging.ui.ConversationListActivity",
            iconRes = R.drawable.ic_messages,
        ),
        IconPackEntry(
            label = "Camera",
            packageName = "com.google.android.GoogleCamera",
            activityName = "com.android.camera.CameraLauncher",
            iconRes = R.drawable.ic_camera,
        ),
        IconPackEntry(
            label = "Photos",
            packageName = "com.google.android.apps.photos",
            activityName = "com.google.android.apps.photos.home.HomeActivity",
            iconRes = R.drawable.ic_photos,
        ),
        IconPackEntry(
            label = "Chrome",
            packageName = "com.android.chrome",
            activityName = "com.google.android.apps.chrome.Main",
            iconRes = R.drawable.ic_chrome,
        ),
        IconPackEntry(
            label = "Gmail",
            packageName = "com.google.android.gm",
            activityName = "com.google.android.gm.ConversationListActivityGmail",
            iconRes = R.drawable.ic_gmail,
        ),
        IconPackEntry(
            label = "Calendar",
            packageName = "com.google.android.calendar",
            activityName = "com.android.calendar.AllInOneActivity",
            iconRes = R.drawable.ic_calendar,
        ),
        IconPackEntry(
            label = "Clock",
            packageName = "com.google.android.deskclock",
            activityName = "com.android.deskclock.DeskClock",
            iconRes = R.drawable.ic_clock,
        ),
        IconPackEntry(
            label = "Settings",
            packageName = "com.android.settings",
            activityName = "com.android.settings.Settings",
            iconRes = R.drawable.ic_settings,
        ),
        IconPackEntry(
            label = "Files",
            packageName = "com.google.android.documentsui",
            activityName = "com.android.documentsui.files.FilesActivity",
            iconRes = R.drawable.ic_files,
        ),
        IconPackEntry(
            label = "Calculator",
            packageName = "com.google.android.calculator",
            activityName = "com.android.calculator2.Calculator",
            iconRes = R.drawable.ic_calculator,
        ),
        IconPackEntry(
            label = "Maps",
            packageName = "com.google.android.apps.maps",
            activityName = "com.google.android.maps.MapsActivity",
            iconRes = R.drawable.ic_maps,
        ),
        IconPackEntry(
            label = "YouTube",
            packageName = "com.google.android.youtube",
            activityName = "com.google.android.youtube.HomeActivity",
            iconRes = R.drawable.ic_youtube,
        ),
        IconPackEntry(
            label = "Play Store",
            packageName = "com.android.vending",
            activityName = "com.google.android.finsky.activities.MainActivity",
            iconRes = R.drawable.ic_play_store,
        ),
        IconPackEntry(
            label = "Drive",
            packageName = "com.google.android.apps.docs",
            activityName = "com.google.android.apps.docs.drive.startup.StartupActivity",
            iconRes = R.drawable.ic_drive,
        ),
        IconPackEntry(
            label = "Spotify",
            packageName = "com.spotify.music",
            activityName = "com.spotify.music.MainActivity",
            iconRes = R.drawable.ic_spotify,
        ),
        IconPackEntry(
            label = "WhatsApp",
            packageName = "com.whatsapp",
            activityName = "com.whatsapp.Main",
            iconRes = R.drawable.ic_whatsapp,
        ),
        IconPackEntry(
            label = "Signal",
            packageName = "org.thoughtcrime.securesms",
            activityName = "org.thoughtcrime.securesms.RoutingActivity",
            iconRes = R.drawable.ic_signal,
        ),
        IconPackEntry(
            label = "Telegram",
            packageName = "org.telegram.messenger",
            activityName = "org.telegram.ui.LaunchActivity",
            iconRes = R.drawable.ic_telegram,
        ),
        IconPackEntry(
            label = "ChatGPT",
            packageName = "com.openai.chatgpt",
            activityName = "com.openai.chatgpt.MainActivity",
            iconRes = R.drawable.ic_chatgpt,
        ),
        IconPackEntry(
            label = "Home Assistant",
            packageName = "io.homeassistant.companion.android",
            activityName = "io.homeassistant.companion.android.launch.LaunchActivity",
            iconRes = R.drawable.ic_home_assistant,
        ),
        IconPackEntry(
            label = "Netflix",
            packageName = "com.netflix.mediaclient",
            activityName = "com.netflix.mediaclient.ui.launch.UIWebViewActivity",
            iconRes = R.drawable.ic_netflix,
        ),
    )
}
