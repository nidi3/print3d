package guru.nidi.print3d.aster

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import guru.nidi.print3d.PixelSource
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

internal class Pages(val raf: RandomAccessFile, exist: Boolean, val scale: Int, val useCache: Boolean) {
    val resolution = 3600 / scale
    val pageSize = 2 * (resolution + 1) * (resolution + 1)
    private val indexSize = 360 * 180 * 8
    private val index = ByteArray(indexSize)
    private val cache = CacheBuilder.newBuilder()
            .maximumSize(300)
            .build(object : CacheLoader<Int, ByteBuffer>() {
                override fun load(key: Int) =
                        raf.channel.map(FileChannel.MapMode.READ_ONLY, indexSize + key.toLong() * pageSize, pageSize.toLong())
            })!!

    init {
        if (exist) raf.read(index)
        else raf.write(index)
    }

    operator fun get(lat: Int, lng: Int): Page = getPage(indexOf(lat, lng))

    operator fun set(lat: Int, lng: Int, page: Page) = setPage(indexOf(lat, lng), page)

    fun getTile(page: Page) =
            if (useCache) BufferTile(readBuffer(page), resolution)
            else FileTile(raf, page.value, resolution)

    fun setData(page: Page, value: PixelSource) =
            BufferTile(writeBuffer(page), resolution).write(value)

    private fun readBuffer(page: Page): ByteBuffer = cache.get(((page.value - indexSize) / pageSize).toInt())
    private fun writeBuffer(page: Page): ByteBuffer = raf.channel.map(FileChannel.MapMode.READ_WRITE, page.value, pageSize.toLong())

    private fun indexOf(lat: Int, lng: Int): Int = ((lng + 180) * 180 + 90 + lat) * 8

    private fun getPage(p: Int): Page {
        fun getInt(p: Int): Int =
                (index[p].toInt() shl 24) + ((index[p + 1].toInt() and 0xff) shl 16) + ((index[p + 2].toInt() and 0xff) shl 8) + ((index[p + 3].toInt() and 0xff) shl 0)

        return Page.of((getInt(p).toLong() shl 32) + (getInt(p + 4).toLong() and 0xFFFFFFFF))
    }

    private fun setPage(p: Int, s: Page) {
        val value = s.rawValue
        index[p + 0] = (value ushr 56).toByte()
        index[p + 1] = (value ushr 48).toByte()
        index[p + 2] = (value ushr 40).toByte()
        index[p + 3] = (value ushr 32).toByte()
        index[p + 4] = (value ushr 24).toByte()
        index[p + 5] = (value ushr 16).toByte()
        index[p + 6] = (value ushr 8).toByte()
        index[p + 7] = (value ushr 0).toByte()
        raf.seek(p.toLong())
        raf.write(index, p, 8)
    }
}

sealed class Page(val rawValue: Long) {
    companion object {
        val highBit = 1.toLong() shl 63
        fun of(v: Long) = if ((v and highBit) == 0.toLong()) TimestampPage(v) else PosPage(v and 0x7fffffffffffffffL)
    }

    abstract val value: Long

    abstract fun posOrElse(elsePos: () -> Long): PosPage

    class PosPage(override val value: Long) : Page(value or highBit) {
        override fun posOrElse(elsePos: () -> Long): PosPage = this
    }

    class TimestampPage(override val value: Long) : Page(value) {
        override fun posOrElse(elsePos: () -> Long): PosPage = PosPage(elsePos())
    }
}