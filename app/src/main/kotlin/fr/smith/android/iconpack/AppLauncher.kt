package fr.smith.android.iconpack

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

/** Liste les applications du catalogue réellement présentes sur l'appareil. */
fun interface InstalledAppsProvider {
    /**
     * @param candidates paquets à tester ; seuls ceux-ci sont interrogés, et ils sont tous
     *   déclarés dans `<queries>` — l'application ne demande jamais la visibilité globale.
     * @return sous-ensemble de [candidates] installé et lançable.
     */
    fun installedPackages(candidates: List<String>): Set<String>
}

/** Implémentation réelle, adossée au [PackageManager]. */
class PackageManagerInstalledApps(context: Context) : InstalledAppsProvider {

    private val packageManager = context.applicationContext.packageManager

    override fun installedPackages(candidates: List<String>): Set<String> =
        candidates.filterTo(mutableSetOf()) { packageName ->
            packageManager.getLaunchIntentForPackage(packageName) != null
        }
}

/**
 * Ouvre une application du catalogue.
 *
 * Toute la surface d'échec est absorbée ici : application absente, activité retirée entre
 * l'affichage et le clic, restriction de profil. L'appelant reçoit un booléen, jamais une
 * exception — un clic sur une tuile ne doit pas pouvoir faire tomber l'écran d'accueil.
 */
class AppLauncher(private val context: Context) {

    fun launch(packageName: String): Boolean {
        val intent = try {
            context.packageManager.getLaunchIntentForPackage(packageName)
        } catch (error: RuntimeException) {
            Log.w(TAG, "Résolution impossible pour $packageName", error)
            null
        } ?: return false

        return try {
            context.startActivity(intent)
            true
        } catch (error: ActivityNotFoundException) {
            Log.w(TAG, "Activité introuvable pour $packageName", error)
            false
        } catch (error: SecurityException) {
            Log.w(TAG, "Lancement refusé pour $packageName", error)
            false
        }
    }

    private companion object {
        const val TAG = "AppLauncher"
    }
}
