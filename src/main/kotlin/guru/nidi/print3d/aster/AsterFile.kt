package guru.nidi.print3d.aster

import guru.nidi.print3d.*
import guru.nidi.print3d.aster.Page.PosPage
import guru.nidi.print3d.aster.Page.TimestampPage
import java.io.File
import java.io.RandomAccessFile

class AsterFile(basedir: File, val scale: Int, val sourceProvider: PixelSourceProvider?, val useCache: Boolean,
                val minTime: Long = System.currentTimeMillis(), val updateOnly: Boolean = false) : AutoCloseable {
    private val resolution = 3600 / scale
    private val file = File(basedir, "aster-$resolution.ast")
    private val raf = RandomAccessFile(file, if (sourceProvider == null) "r" else "rw")
    private val pages = Pages(raf, file.length() > 0, scale, useCache)

    val coordStep = 1.0 / resolution

    override fun close() = raf.close()

    fun getPixel(p: LatLng): Int = getPixel(p.lat, p.lng)

    internal fun getTile(lat: Int, lng: Int): Tile? {
        val page = pages[lat, lng]
        return when (page) {
            is PosPage -> pages.getTile(page)
            is TimestampPage -> null
        }
    }

    private fun getPixel(lat: Double, lng: Double): Int {
        fun intPart(v: Double) = Math.floor(v).toInt()
        val slat = intPart(lat)
        val slng = intPart(lng)
        return getPixel(slat, slng, (resolution * (lng - slng)).toInt(), (resolution * (1 - lat + slat)).toInt()).toInt()
    }

    private fun getPixel(lat: Int, lng: Int, x: Int, y: Int): Int {
        fun eval(page: Page): Int = when (page) {
            is PosPage -> if (updateOnly) 0 else pages.getTile(page).get(x, y)
            is TimestampPage -> if (page.value < minTime) eval(importTile(lat, lng)) else -5000
        }
        return eval(pages[lat, lng])
    }

    private fun importTile(lat: Int, lng: Int): Page {
        val source = sourceProvider?.sourceFor(lat, lng)?.let { ScalingPixelSource(it, scale) }
        return if (source != null) doImportTile(lat, lng, source)
        else TimestampPage(System.currentTimeMillis()).also {
            pages[lat, lng] = it
        }
    }

    private fun doImportTile(lat: Int, lng: Int, source: PixelSource): Page {
        return pages[lat, lng].posOrElse { raf.length() }.also {
            pages.setData(it, source)
            pages[lat, lng] = it
        }
    }
}