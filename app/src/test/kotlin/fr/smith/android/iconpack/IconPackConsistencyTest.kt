package fr.smith.android.iconpack

import fr.smith.android.R
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `appfilter.xml`, `drawable.xml` et [IconPackCatalog] sont dérivés de `tools/iconpack.json`
 * par `tools/gen_iconpack.py`. Ce test échoue si l'un des trois a été édité à la main : sans
 * lui, une divergence ne se verrait qu'au moment où un launcher n'applique pas une icône,
 * c'est-à-dire jamais en CI.
 *
 * Le répertoire de travail des tests unitaires est le module `app/`.
 */
class IconPackConsistencyTest {

    private val moduleDir = File(".").absoluteFile.normalize()
    private val appFilter = File(moduleDir, "src/main/res/xml/appfilter.xml")
    private val drawableIndex = File(moduleDir, "src/main/res/xml/drawable.xml")
    private val drawableDir = File(moduleDir, "src/main/res/drawable")

    @Test
    fun `le catalogue n est pas vide`() {
        assertTrue(IconPackCatalog.entries.isNotEmpty())
    }

    @Test
    fun `appfilter declare exactement les memes composants que le catalogue`() {
        val declared = COMPONENT.findAll(appFilter.readText()).map { it.groupValues[1] }.toList()
        assertEquals(IconPackCatalog.entries.map { it.component }, declared)
    }

    @Test
    fun `chaque entree du catalogue pointe vers un drawable existant`() {
        val missing = IconPackCatalog.entries.filterNot { entry ->
            File(drawableDir, "${drawableName(entry)}.xml").exists()
        }
        assertEquals(emptyList<IconPackEntry>(), missing)
    }

    @Test
    fun `le selecteur d icones liste toutes les icones du pack`() {
        val listed = DRAWABLE.findAll(drawableIndex.readText()).map { it.groupValues[1] }.toList()
        assertEquals(IconPackCatalog.entries.map { drawableName(it) }, listed)
    }

    @Test
    fun `le fond et le masque de repli existent`() {
        assertTrue(File(drawableDir, "iconback_smith.xml").exists())
        assertTrue(File(drawableDir, "iconmask_smith.xml").exists())
    }

    @Test
    fun `les paquets et les drawables sont uniques`() {
        val entries = IconPackCatalog.entries
        assertEquals(entries.size, entries.map { it.packageName }.toSet().size)
        assertEquals(entries.size, entries.map { it.iconRes }.toSet().size)
    }

    /** Retrouve le nom de ressource à partir de l'identifiant compilé. */
    private fun drawableName(entry: IconPackEntry): String {
        val field = R.drawable::class.java.fields.first { it.getInt(null) == entry.iconRes }
        return field.name
    }

    private companion object {
        val COMPONENT = Regex("""component="(ComponentInfo\{[^}]+})"""")
        val DRAWABLE = Regex("""<item drawable="([^"]+)"\s*/>""")
    }
}
