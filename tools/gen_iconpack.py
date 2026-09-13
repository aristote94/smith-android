#!/usr/bin/env python3
"""Génère tout ce qui dérive de tools/iconpack.json.

Un seul fichier décrit le pack ; ce script en dérive :
  - app/src/main/res/xml/appfilter.xml      (lu par les launchers)
  - app/src/main/res/xml/drawable.xml       (sélecteur d'icônes des launchers)
  - app/src/main/kotlin/.../IconPackCatalog.kt (aperçu dans l'application)
  - le bloc <queries> d'AndroidManifest.xml (visibilité des paquets, API 30+)

Aucune de ces quatre représentations ne peut donc diverger.
Usage : python3 tools/gen_iconpack.py [--check]
"""
import json
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SPEC = os.path.join(ROOT, "tools", "iconpack.json")
RES = os.path.join(ROOT, "app", "src", "main", "res")
MANIFEST = os.path.join(ROOT, "app", "src", "main", "AndroidManifest.xml")
KOTLIN = os.path.join(
    ROOT, "app", "src", "main", "kotlin", "fr", "smith", "android",
    "iconpack", "IconPackCatalog.kt",
)
BANNER = "Généré par tools/gen_iconpack.py depuis tools/iconpack.json — ne pas éditer."

with open(SPEC, encoding="utf-8") as handle:
    spec = json.load(handle)
entries = spec["entries"]

packages = [e["package"] for e in entries]
drawables = [e["drawable"] for e in entries]
if len(set(packages)) != len(packages):
    sys.exit("paquets dupliqués dans iconpack.json")
if len(set(drawables)) != len(drawables):
    sys.exit("drawables dupliqués dans iconpack.json")
missing = [d for d in drawables + [spec["iconback"], spec["iconmask"]]
           if not os.path.exists(os.path.join(RES, "drawable", f"{d}.xml"))]
if missing:
    sys.exit(f"drawables absents de res/drawable : {missing}")


def appfilter() -> str:
    items = "\n".join(
        f'    <item component="ComponentInfo{{{e["package"]}/{e["activity"]}}}"'
        f' drawable="{e["drawable"]}" />'
        for e in entries
    )
    return f"""<?xml version="1.0" encoding="utf-8"?>
<!-- {BANNER} -->
<!-- Résolu par nom depuis les launchers tiers : invisible du code, donc de lint. -->
<resources
    xmlns:tools="http://schemas.android.com/tools"
    tools:ignore="UnusedResources">

    <!-- Appliqué aux applications absentes de la liste : fond, masque et
         réduction du logo d'origine, pour que tout l'écran reste homogène. -->
    <iconback img1="{spec['iconback']}" />
    <iconmask img1="{spec['iconmask']}" />
    <scale factor="{spec['scale']}" />

{items}

</resources>
"""


def drawable_xml() -> str:
    items = "\n".join(f'        <item drawable="{d}" />' for d in drawables)
    return f"""<?xml version="1.0" encoding="utf-8"?>
<!-- {BANNER} -->
<!-- Résolu par nom depuis les launchers tiers : invisible du code, donc de lint. -->
<resources
    xmlns:tools="http://schemas.android.com/tools"
    tools:ignore="UnusedResources">
    <category title="SMITH" />
{items}
</resources>
"""


def kotlin() -> str:
    items = "\n".join(
        f"""        IconPackEntry(
            label = "{e['label']}",
            packageName = "{e['package']}",
            activityName = "{e['activity']}",
            iconRes = R.drawable.{e['drawable']},
        ),"""
        for e in entries
    )
    return f"""package fr.smith.android.iconpack

import androidx.annotation.DrawableRes
import fr.smith.android.R

/**
 * {BANNER}
 *
 * Entrée du pack : une icône SMITH et l'application qu'elle habille.
 */
data class IconPackEntry(
    val label: String,
    val packageName: String,
    val activityName: String,
    @DrawableRes val iconRes: Int,
) {{
    /** Forme attendue par `appfilter.xml`, utilisée pour vérifier la cohérence en test. */
    val component: String get() = "ComponentInfo{{$packageName/$activityName}}"
}}

/** Catalogue complet du pack, dans l'ordre d'affichage. */
object IconPackCatalog {{
    val entries: List<IconPackEntry> = listOf(
{items}
    )
}}
"""


def queries_block() -> str:
    items = "\n".join(f'        <package android:name="{p}" />' for p in packages)
    return (
        "    <!-- BEGIN generated queries -->\n"
        f"    <!-- {BANNER} -->\n"
        "    <queries>\n"
        f"{items}\n"
        "    </queries>\n"
        "    <!-- END generated queries -->"
    )


def patch_manifest(text: str) -> str:
    pattern = re.compile(
        r"    <!-- BEGIN generated queries -->.*?    <!-- END generated queries -->",
        re.DOTALL,
    )
    if not pattern.search(text):
        sys.exit("marqueurs <!-- BEGIN/END generated queries --> absents du manifeste")
    return pattern.sub(lambda _: queries_block(), text)


targets = {
    os.path.join(RES, "xml", "appfilter.xml"): appfilter(),
    os.path.join(RES, "xml", "drawable.xml"): drawable_xml(),
    KOTLIN: kotlin(),
}
with open(MANIFEST, encoding="utf-8") as handle:
    targets[MANIFEST] = patch_manifest(handle.read())

check = "--check" in sys.argv
stale = []
for path, content in targets.items():
    current = None
    if os.path.exists(path):
        with open(path, encoding="utf-8") as handle:
            current = handle.read()
    if current == content:
        continue
    stale.append(os.path.relpath(path, ROOT))
    if not check:
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, "w", encoding="utf-8") as handle:
            handle.write(content)

if check and stale:
    sys.exit("fichiers générés obsolètes : " + ", ".join(stale))
print(f"{len(entries)} entrées — " + ("à jour" if not stale else "régénéré : " + ", ".join(stale)))
