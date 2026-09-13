# SMITH Android

Pack d'icônes Android au style industriel sobre, accompagné d'un widget d'accueil.
Sans réseau, sans permission, sans donnée personnelle.

| | |
|---|---|
| Langage | Kotlin 2.0.21 |
| Interface | Jetpack Compose (Material 3) |
| Widget | Jetpack Glance |
| Android | minSdk 26 · targetSdk 35 |
| Build | Gradle 8.11.1 (wrapper) · AGP 8.7.3 · JDK 17 |

## Installer

### Depuis une release

Les APK signés sont publiés dans [Releases](../../releases). Télécharger le `.apk`,
l'installer, puis dans le launcher : **Paramètres → Apparence → Pack d'icônes → SMITH**.

### Depuis la CI

Chaque build de `main` publie un APK de debug dans l'onglet **Actions** (artefact
`smith-debug-apk`). Il porte l'identifiant `fr.smith.android.debug` et cohabite donc avec
une version installée depuis une release.

## Launchers compatibles

Le pack utilise le format ADW/Nova (`appfilter.xml` + `drawable.xml`), lu par :

**Lawnchair** (cible principale — libre, Android 8+, icônes thématiques), **Nova**,
**Niagara**, **Smart Launcher**, **Action Launcher**, **Apex**.

Les applications absentes du catalogue reçoivent tout de même le fond et le masque SMITH
(`iconback` / `iconmask` / `scale`), pour que l'écran d'accueil reste homogène.

## Développer

```bash
./gradlew :app:assembleDebug          # APK de debug
./gradlew :app:testDebugUnitTest      # tests JVM
./gradlew :app:lintDebug              # lint (warningsAsErrors)
./gradlew :app:connectedDebugAndroidTest   # tests d'interface, appareil requis
```

Prérequis : JDK 17 et un SDK Android 35 (`ANDROID_HOME`, ou `sdk.dir` dans
`local.properties`). Gradle est fourni par le wrapper, rien d'autre à installer.

### Structure

```
app/src/main/kotlin/fr/smith/android/
├── domain/        modèle et contrat de données — aucun import Android
├── data/demo/     source de démonstration, déterministe (horloge injectée)
├── core/format/   mise en forme des nombres et des dates, testable sur JVM
├── iconpack/      catalogue du pack, lancement d'applications
├── di/            graphe de dépendances (injection manuelle)
├── ui/            Compose : thème, écran d'accueil, composants
└── widget/        widget Glance, alimenté par le même dépôt que l'écran
```

L'écran et le widget consomment le **même** `HomeRepository` : une seule définition des
données, donc pas de valeurs divergentes entre les deux surfaces.

### Modifier le pack d'icônes

`tools/iconpack.json` est la source unique. Après édition :

```bash
python3 tools/gen_icons.py       # vecteurs des icônes
python3 tools/gen_iconpack.py    # appfilter.xml, drawable.xml, IconPackCatalog.kt, <queries>
```

Ne jamais éditer les fichiers générés à la main : la CI régénère et échoue sur tout écart,
et `IconPackConsistencyTest` verrouille la cohérence des trois représentations.

Pour ajouter une icône : ajouter le glyphe dans `ICONS` (`tools/gen_icons.py`), l'entrée
correspondante dans `tools/iconpack.json`, régénérer.

## Publier une release

1. Créer un keystore (une fois) :
   ```bash
   keytool -genkeypair -v -keystore smith.jks -keyalg RSA -keysize 4096 \
     -validity 10000 -alias smith
   ```
2. Dépôt → **Settings → Secrets and variables → Actions**, ajouter :
   `SMITH_KEYSTORE_BASE64` (`base64 -w0 smith.jks`), `SMITH_KEYSTORE_PASSWORD`,
   `SMITH_KEY_ALIAS`, `SMITH_KEY_PASSWORD`.
3. Incrémenter `versionCode` et `versionName` dans `app/build.gradle.kts`.
4. `git tag v0.2.0 && git push --tags`

Le workflow `Release` construit un APK signé (R8 + rétrécissement des ressources), vérifie
la signature, et l'attache à la release GitHub. Il échoue avant de construire si un secret
manque, pour ne jamais publier un APK non signé.

En local, créer `keystore.properties` à la racine (jamais versionné) :

```properties
storeFile=/chemin/vers/smith.jks
storePassword=…
keyAlias=smith
keyPassword=…
```

Sans ce fichier ni les variables d'environnement, `assembleRelease` fonctionne quand même
mais produit un APK **non signé** : pratique pour vérifier R8, inutilisable pour distribuer.

## Données

Les valeurs affichées (température, énergie, caméras) sont produites par
`DemoHomeRepository` à partir de l'horloge du téléphone. Elles sont **déterministes** : deux
lectures au même instant donnent le même résultat, ce qui rend les tests exacts.

Une intégration Home Assistant se branchera en ajoutant une implémentation de
`HomeRepository` dans `di/AppContainer.kt` — l'interface, l'écran et le widget ne bougent pas.
