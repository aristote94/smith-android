package fr.smith.android

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import kotlin.math.min

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(Color.rgb(21, 23, 25)))
        setContentView(SmithPreview(this))
    }
}

private class SmithPreview(context: Context) : View(context) {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bg = Color.rgb(21, 23, 25)
    private val panel = Color.rgb(32, 35, 38)
    private val text = Color.rgb(230, 232, 231)
    private val muted = Color.rgb(156, 164, 167)
    private val steel = Color.rgb(111, 135, 146)
    private val ok = Color.rgb(112, 146, 122)
    private val apps = listOf("Téléphone", "Messages", "Caméra", "Maison", "Chrome", "Gmail", "Agenda", "Réglages", "Spotify", "ChatGPT", "Photos", "Fichiers")

    override fun onDraw(c: Canvas) {
        c.drawColor(bg)
        val w = width.toFloat()
        val sx = w / 1080f
        c.save(); c.scale(sx, sx)
        val h = height / sx
        val pad = 70f
        label(c, "S  M  I  T  H", pad, 80f, 24f, muted)
        label(c, "09:27", pad, 205f, 92f, text)
        label(c, "Maison · mardi 16 avril", pad, 250f, 24f, muted)

        val top = 300f
        rounded(c, pad, top, 1010f, top + 300f, 32f, panel, steel)
        label(c, "Maison", 105f, top + 62f, 36f, text, true)
        dot(c, 755f, top + 49f, 9f, ok)
        label(c, "Tout va bien", 778f, top + 59f, 22f, ok)
        line(c, 105f, top + 92f, 975f, top + 92f, steel, 1.4f)
        label(c, "21,4°", 105f, top + 185f, 58f, text, true)
        label(c, "Salon · humidité 42 %", 105f, top + 225f, 22f, muted)
        label(c, "Énergie", 650f, top + 155f, 22f, muted)
        label(c, "1,2 kW", 650f, top + 195f, 38f, text, true)
        label(c, "4 caméras en ligne", 105f, top + 270f, 23f, text)

        val startY = top + 375f
        val cellW = 235f
        val icon = 132f
        apps.forEachIndexed { i, name ->
            val col = i % 4; val row = i / 4
            val cx = pad + col * cellW + cellW / 2
            val y = startY + row * 205f
            rounded(c, cx-icon/2, y, cx+icon/2, y+icon, 30f, panel, steel)
            glyph(c, name, cx, y + icon/2)
            centered(c, name, cx, y + icon + 40f, 20f, text)
        }
        label(c, "Interface graphique · données de démonstration", pad, h - 34f, 18f, muted)
        c.restore()
    }

    private fun glyph(c: Canvas, name: String, x: Float, y: Float) {
        p.style=Paint.Style.STROKE; p.strokeWidth=7f; p.strokeCap=Paint.Cap.ROUND; p.strokeJoin=Paint.Join.ROUND; p.color=text
        when(name) {
            "Téléphone" -> c.drawArc(x-29,y-34,x+29,y+34,35f,115f,false,p)
            "Messages" -> { c.drawRoundRect(x-34,y-24,x+34,y+22,10f,10f,p); c.drawLine(x-12,y+22,x-22,y+34,p) }
            "Caméra", "Photos" -> { c.drawRoundRect(x-35,y-24,x+35,y+26,8f,8f,p); c.drawCircle(x,y+1,14f,p) }
            "Maison" -> { val q=Path(); q.moveTo(x-35,y);q.lineTo(x,y-32);q.lineTo(x+35,y);q.lineTo(x+35,y+33);q.lineTo(x-35,y+33);q.close();c.drawPath(q,p) }
            "Spotify" -> { c.drawCircle(x,y,35f,p);c.drawArc(x-23,y-16,x+24,y+11,205f,115f,false,p);c.drawArc(x-19,y-2,x+20,y+20,205f,115f,false,p) }
            "ChatGPT" -> { c.drawCircle(x,y,31f,p);c.drawLine(x-25,y-15,x+25,y+15,p);c.drawLine(x-25,y+15,x+25,y-15,p) }
            else -> { c.drawCircle(x,y,30f,p); c.drawCircle(x,y,7f,p) }
        }
        p.style=Paint.Style.FILL
    }
    private fun rounded(c:Canvas,l:Float,t:Float,r:Float,b:Float,rad:Float,fill:Int,stroke:Int){p.style=Paint.Style.FILL;p.color=fill;c.drawRoundRect(l,t,r,b,rad,rad,p);p.style=Paint.Style.STROKE;p.strokeWidth=1.5f;p.color=stroke;c.drawRoundRect(l,t,r,b,rad,rad,p);p.style=Paint.Style.FILL}
    private fun label(c:Canvas,s:String,x:Float,y:Float,size:Float,color:Int,bold:Boolean=false){p.color=color;p.textSize=size;p.typeface=Typeface.create("sans",if(bold)Typeface.BOLD else Typeface.NORMAL);c.drawText(s,x,y,p)}
    private fun centered(c:Canvas,s:String,x:Float,y:Float,size:Float,color:Int){p.textAlign=Paint.Align.CENTER;label(c,s,x,y,size,color);p.textAlign=Paint.Align.LEFT}
    private fun dot(c:Canvas,x:Float,y:Float,r:Float,color:Int){p.color=color;c.drawCircle(x,y,r,p)}
    private fun line(c:Canvas,x1:Float,y1:Float,x2:Float,y2:Float,color:Int,sw:Float){p.color=color;p.strokeWidth=sw;c.drawLine(x1,y1,x2,y2,p)}
}
