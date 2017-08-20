package guru.nidi.print3d.aster

import guru.nidi.print3d.LatLng
import guru.nidi.print3d.PixelSource
import guru.nidi.print3d.PixelSourceProvider
import guru.nidi.print3d.aster.Slot.PosSlot
import guru.nidi.print3d.aster.Slot.TimestampSlot
import java.io.File
import java.io.RandomAccessFile

class AsterFile(basedir: File, val scale: Int, val sourceProvider: PixelSourceProvider, val useCache: Boolean, val minTime: Long = System.currentTimeMillis()) : AutoCloseable {
    private val resolution = 3600 / scale
    private val file = File(basedir, "aster-$resolution.ast")
    private val raf = RandomAccessFile(file, "rw")
    private val slots = Slots(raf, file.length() > 0, 2 * (resolution + 1) * (resolution + 1))

    val coordStep = 1.0 / resolution

    override fun close() = raf.close()

    fun getPixel(p: LatLng): Int = getPixel(p.lat, p.lng)

    internal fun getTile(lat: Int, lng: Int): Tile? {
        val slot = slots[lat, lng]
        return when (slot) {
            is PosSlot -> Tile(slots.pageToRead(slot), resolution)
            is TimestampSlot -> null
        }
    }

    private fun getPixel(lat: Double, lng: Double): Int {
        fun intPart(v: Double) = Math.floor(v).toInt()
        val slat = intPart(lat)
        val slng = intPart(lng)
        return getPixel(slat, slng, (resolution * (lng - slng)).toInt(), (resolution * (1 - lat + slat)).toInt()).toInt()
    }

    private fun getPixel(lat: Int, lng: Int, x: Int, y: Int): Short {
        fun eval(slot: Slot): Short = when (slot) {
            is PosSlot -> {
                if (useCache) {
                    slots.pageToRead(slot).getShort(dataPos(x, y))
                } else {
                    raf.seek(slot.value + dataPos(x, y))
                    raf.readShort()
                }
            }
            is TimestampSlot -> if (slot.value < minTime) eval(importTile(lat, lng)) else -5000
        }
        return eval(slots[lat, lng])
    }

    private fun importTile(lat: Int, lng: Int): Slot {
        val source = sourceProvider.sourceFor(lat, lng)?.let { ScalingPixelSource(it, scale) }
        return if (source != null) doImportTile(lat, lng, source)
        else {
            val slot = TimestampSlot(System.currentTimeMillis())
            slots[lat, lng] = slot
            slot
        }
    }

    private fun doImportTile(lat: Int, lng: Int, source: PixelSource): Slot {
        val newSlot = slots[lat, lng].posOrElse { raf.length() }
        setData(newSlot, source)
        slots[lat, lng] = newSlot
        return newSlot
    }

    private fun setData(slot: Slot, value: PixelSource) {
        val data = slots.pageToWrite(slot)
        var y = 0
        while (y < resolution + 1) {
            value.doWithLine(y) { x, pixel ->
                val pos = dataPos(x, y)
                data.putShort(pos, pixel.toShort())
            }
            y += 1
        }
    }

    private fun dataPos(x: Int, y: Int) = (x + (resolution + 1) * y) * 2

}