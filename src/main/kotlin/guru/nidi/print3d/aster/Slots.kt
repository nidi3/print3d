package guru.nidi.print3d.aster

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

internal class Slots(val raf: RandomAccessFile, exist: Boolean, val pageSize: Int) {
    private val slotsSize = 360 * 180 * 8
    private val slots = ByteArray(slotsSize)
    private val cache = CacheBuilder.newBuilder()
            .maximumSize(1000000000L / pageSize)
            .build(object : CacheLoader<Int, ByteBuffer>() {
                override fun load(key: Int) =
                        raf.channel.map(FileChannel.MapMode.READ_ONLY, slotsSize + key.toLong() * pageSize, pageSize.toLong())
            })!!

    init {
        if (exist) raf.read(slots)
        else raf.write(slots)
    }

    operator fun get(lat: Int, lng: Int): Slot = getSlot(slotOf(lat, lng))

    operator fun set(lat: Int, lng: Int, slot: Slot) = setSlot(slotOf(lat, lng), slot)

    fun pageToRead(slot: Slot): ByteBuffer = cache.get(((slot.value - slotsSize) / pageSize).toInt())

    fun pageToWrite(slot: Slot): ByteBuffer = raf.channel.map(FileChannel.MapMode.READ_WRITE, slot.value, pageSize.toLong())

    private fun slotOf(lat: Int, lng: Int): Int = ((lng + 180) * 180 + 90 + lat) * 8

    private fun getSlot(p: Int): Slot {
        fun getInt(p: Int): Int =
                (slots[p].toInt() shl 24) + ((slots[p + 1].toInt() and 0xff) shl 16) + ((slots[p + 2].toInt() and 0xff) shl 8) + ((slots[p + 3].toInt() and 0xff) shl 0)

        return Slot.of((getInt(p).toLong() shl 32) + (getInt(p + 4).toLong() and 0xFFFFFFFF))
    }

    private fun setSlot(p: Int, slot: Slot) {
        val value = slot.rawValue
        slots[p + 0] = (value ushr 56).toByte()
        slots[p + 1] = (value ushr 48).toByte()
        slots[p + 2] = (value ushr 40).toByte()
        slots[p + 3] = (value ushr 32).toByte()
        slots[p + 4] = (value ushr 24).toByte()
        slots[p + 5] = (value ushr 16).toByte()
        slots[p + 6] = (value ushr 8).toByte()
        slots[p + 7] = (value ushr 0).toByte()
        raf.seek(p.toLong())
        raf.write(slots, p, 8)
    }
}

sealed class Slot(val rawValue: Long) {
    companion object {
        val highBit = 1.toLong() shl 63
        fun of(v: Long) = if ((v and highBit) == 0.toLong()) TimestampSlot(v) else PosSlot(v and 0x7fffffffffffffffL)
    }

    abstract val value: Long

    abstract fun posOrElse(elsePos: () -> Long): PosSlot

    class PosSlot(override val value: Long) : Slot(value or highBit) {
        override fun posOrElse(elsePos: () -> Long): PosSlot = this
    }

    class TimestampSlot(override val value: Long) : Slot(value) {
        override fun posOrElse(elsePos: () -> Long): PosSlot = PosSlot(elsePos())
    }
}