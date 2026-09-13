# R8 en mode optimisé. Compose, Glance et AndroidX embarquent leurs propres
# règles consumer : rien à répéter ici.

# Les noms des composants exposés aux launchers tiers (pack d'icônes) et au
# gestionnaire de widgets sont résolus par réflexion depuis le manifeste.
# AGP les conserve via le manifest keep rules, mais on l'ancre explicitement :
# une renommée silencieuse casserait l'installation du widget sans erreur de build.
-keep class fr.smith.android.MainActivity
-keep class fr.smith.android.widget.SmithWidgetReceiver

# Conserver les numéros de ligne pour des stack traces exploitables en release.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
