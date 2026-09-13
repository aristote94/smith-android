#!/usr/bin/env python3
"""Génère les vecteurs du pack d'icônes SMITH.

Chaque icône = un fond (carré arrondi, plein écran de l'icône) + un glyphe filaire.
Le style est centralisé ici pour que les 22 icônes restent rigoureusement cohérentes.
"""
import os, math

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "app", "src", "main", "res", "drawable")

PANEL = "#FF202326"
EDGE  = "#FF313A3E"
INK   = "#FFE6E8E7"
ACC   = "#FF6F8792"
OK    = "#FF70927A"

def circle(cx, cy, r):
    return f"M{cx - r},{cy} a{r},{r} 0 1,0 {2*r},0 a{r},{r} 0 1,0 {-2*r},0"

def rrect(l, t, r, b, rad):
    return (f"M{l+rad},{t} H{r-rad} A{rad},{rad} 0 0,1 {r},{t+rad} "
            f"V{b-rad} A{rad},{rad} 0 0,1 {r-rad},{b} H{l+rad} "
            f"A{rad},{rad} 0 0,1 {l},{b-rad} V{t+rad} A{rad},{rad} 0 0,1 {l+rad},{t} Z")

def gear(cx, cy, r_out, r_in, teeth=8, tooth_ratio=0.52):
    """Contour fermé d'un engrenage : dents rectangulaires arrondies par le strokeLineJoin."""
    pts, step = [], 360.0 / teeth
    for i in range(teeth):
        a0 = i * step
        half = step * tooth_ratio / 2
        for r, a in ((r_in, a0 - step / 2 + half / 2), (r_out, a0 - half),
                     (r_out, a0 + half), (r_in, a0 + step / 2 - half / 2)):
            t = math.radians(a)
            pts.append((cx + r * math.cos(t), cy + r * math.sin(t)))
    body = " ".join(f"L{x:.1f},{y:.1f}" for x, y in pts[1:])
    return f"M{pts[0][0]:.1f},{pts[0][1]:.1f} {body} Z"


def spokes(cx, cy, r0, r1, n, start=0.0):
    out = []
    for i in range(n):
        a = math.radians(start + i * 360.0 / n)
        out.append(f"M{cx + r0*math.cos(a):.1f},{cy + r0*math.sin(a):.1f} "
                   f"L{cx + r1*math.cos(a):.1f},{cy + r1*math.sin(a):.1f}")
    return " ".join(out)

# name -> liste de couches : (kind, data, color, width)
#   kind "s" = trait, "f" = aplat
ICONS = {
"phone": [("s", "M33,30 h11 l5,13 -8,6 a34,34 0 0 0 18,18 l6,-8 13,5 v11 a6,6 0 0 1 -6,6 C47,81 27,61 27,36 a6,6 0 0 1 6,-6 z", INK, 6)],

"messages": [
    ("s", "M30,38 a9,9 0 0 1 9,-9 h30 a9,9 0 0 1 9,9 v19 a9,9 0 0 1 -9,9 H47 L33,77 V65 a9,9 0 0 1 -3,-7 z", INK, 6),
    ("f", circle(44, 47, 3.4) + " " + circle(54, 47, 3.4) + " " + circle(64, 47, 3.4), ACC, 0)],

"camera": [
    ("s", "M27,43 a7,7 0 0 1 7,-7 h8 l5,-8 h14 l5,8 h8 a7,7 0 0 1 7,7 v25 a7,7 0 0 1 -7,7 H34 a7,7 0 0 1 -7,-7 z", INK, 6),
    ("s", circle(54, 55, 13), INK, 6)],

"photos": [
    ("s", rrect(27, 31, 81, 77, 8), INK, 6),
    ("s", "M31,68 L46,52 L57,63 L67,53 L77,66", INK, 6),
    ("f", circle(67, 44, 5), ACC, 0)],

"chrome": [
    ("s", circle(54, 54, 25), INK, 6),
    ("s", circle(54, 54, 11), ACC, 6),
    ("s", spokes(54, 54, 11, 25, 3, -90), INK, 6)],

"gmail": [
    ("s", rrect(26, 36, 82, 73, 7), INK, 6),
    ("s", "M28,40 L54,60 L80,40", ACC, 6)],

"calendar": [
    ("s", rrect(28, 33, 80, 78, 8), INK, 6),
    ("s", "M42,25 V40 M66,25 V40 M28,49 H80", INK, 6),
    ("f", circle(44, 62, 4) + " " + circle(58, 62, 4), ACC, 0)],

"clock": [
    ("s", circle(54, 55, 25), INK, 6),
    ("s", "M54,38 V55 L66,63", ACC, 6),
    ("s", "M40,27 L33,33 M68,27 L75,33", INK, 6)],

"settings": [
    ("s", gear(54, 54, 27, 19), INK, 5.5),
    ("s", circle(54, 54, 9), ACC, 5.5)],

"files": [
    ("s", "M26,41 a6,6 0 0 1 6,-6 h15 l8,9 h21 a6,6 0 0 1 6,6 v23 a6,6 0 0 1 -6,6 H32 a6,6 0 0 1 -6,-6 z", INK, 6),
    ("s", "M32,52 H76", ACC, 5)],

"calculator": [
    ("s", rrect(32, 25, 76, 83, 8), INK, 6),
    ("s", rrect(40, 33, 68, 47, 4), ACC, 5),
    ("f", circle(44, 58, 3.6) + " " + circle(54, 58, 3.6) + " " + circle(64, 58, 3.6)
        + " " + circle(44, 70, 3.6) + " " + circle(54, 70, 3.6) + " " + circle(64, 70, 3.6), INK, 0)],

"maps": [
    ("s", "M54,81 C54,81 34,59 34,45 a20,20 0 1 1 40,0 c0,14 -20,36 -20,36 z", INK, 6),
    ("s", circle(54, 45, 8), ACC, 6)],

"youtube": [
    ("s", rrect(24, 36, 84, 73, 12), INK, 6),
    ("f", "M48,44 L69,55 L48,66 Z", ACC, 0)],

"play_store": [
    ("s", "M37,25 L78,54 L37,83 Z", INK, 6),
    ("s", "M37,25 L60,54 L37,83", ACC, 5)],

"drive": [
    ("s", "M54,25 L82,76 H26 Z", INK, 6),
    ("s", "M54,25 L54,59 M54,59 L27,75 M54,59 L81,75", ACC, 5)],

"spotify": [
    ("s", circle(54, 54, 26), INK, 6),
    ("s", "M38,44 Q54,37 71,47", INK, 6),
    ("s", "M41,55 Q54,49 68,57", INK, 5.5),
    ("s", "M44,65 Q54,60 65,67", ACC, 5)],

"whatsapp": [
    ("s", circle(54, 48, 24), INK, 6),
    # Queue de bulle : les deux extrémités reposent sur le cercle, elle ne flotte pas.
    ("s", "M31,56 L25,74 L43,69", INK, 6),
    ("s", "M47,43 a4,4 0 0 1 4,-4 h2 l4,8 -5,4 a17,17 0 0 0 9,9 l4,-5 8,4 v2 "
          "a4,4 0 0 1 -4,4 C54,65 47,55 47,43 z", ACC, 5)],

"signal": [
    ("s", circle(54, 48, 24), INK, 6),
    ("s", "M31,56 L25,74 L43,69", INK, 6),
    ("s", circle(54, 48, 14), ACC, 5)],

"telegram": [
    ("s", "M26,53 L82,29 L71,81 L50,65 Z", INK, 6),
    ("s", "M50,65 L82,29 M50,65 L49,79", ACC, 5)],

"chatgpt": [
    ("s", "M54,25 L79,39 V68 L54,83 L29,68 V39 Z", INK, 6),
    ("s", circle(54, 54, 10), ACC, 5.5)],

"home_assistant": [
    ("s", "M26,54 L54,28 L82,54", INK, 6),
    ("s", "M34,49 V78 H74 V49", INK, 6),
    ("s", "M43,65 Q54,55 65,65", ACC, 5),
    ("s", "M48,71 Q54,66 60,71", ACC, 5),
    ("f", circle(54, 77, 4), OK, 0)],

"netflix": [
    ("s", "M37,25 V83", INK, 7),
    ("s", "M71,25 V83", INK, 7),
    ("s", "M37,25 L71,83", ACC, 7)],
}

VECTOR = """<?xml version="1.0" encoding="utf-8"?>
<!-- Généré par tools/gen_icons.py — ne pas éditer à la main. -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
{extra}    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
{layers}
</vector>
"""

BACKGROUND = f"""    <path
        android:fillColor="{PANEL}"
        android:pathData="{rrect(4, 4, 104, 104, 24)}" />
    <path
        android:fillColor="#00000000"
        android:strokeColor="{EDGE}"
        android:strokeWidth="2"
        android:pathData="{rrect(5, 5, 103, 103, 23)}" />"""

def layer(kind, data, color, width):
    if kind == "f":
        return (f'    <path\n        android:fillColor="{color}"\n'
                f'        android:fillType="evenOdd"\n'
                f'        android:pathData="{data}" />')
    return (f'    <path\n        android:fillColor="#00000000"\n'
            f'        android:strokeColor="{color}"\n'
            f'        android:strokeWidth="{width}"\n'
            f'        android:strokeLineCap="round"\n'
            f'        android:strokeLineJoin="round"\n'
            f'        android:pathData="{data}" />')

os.makedirs(OUT, exist_ok=True)
for name, layers in ICONS.items():
    body = "\n".join([BACKGROUND] + [layer(*l) for l in layers])
    with open(os.path.join(OUT, f"ic_{name}.xml"), "w") as f:
        f.write(VECTOR.format(layers=body, extra=""))

# Fond et masque appliqués par le launcher aux applications non thémées.
# iconback/iconmask ne sont cités que par appfilter.xml, que lint ne sait pas lire.
EXTERNAL = ('    xmlns:tools="http://schemas.android.com/tools"\n'
            '    tools:ignore="UnusedResources"\n')
with open(os.path.join(OUT, "iconback_smith.xml"), "w") as f:
    f.write(VECTOR.format(layers=BACKGROUND, extra=EXTERNAL))
with open(os.path.join(OUT, "iconmask_smith.xml"), "w") as f:
    f.write(VECTOR.format(extra=EXTERNAL, layers=(
        f'    <path\n        android:fillColor="#FFFFFFFF"\n'
        f'        android:pathData="{rrect(4, 4, 104, 104, 24)}" />')))

print(f"{len(ICONS)} icônes + iconback + iconmask")
print(" ".join(sorted(ICONS)))
