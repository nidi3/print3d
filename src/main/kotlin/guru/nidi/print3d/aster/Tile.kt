package guru.nidi.print3d.aster

import guru.nidi.print3d.PixelSource
import java.io.RandomAccessFile
import java.nio.ByteBuffer


internal sealed class Tile(val resolution: Int) : PixelSource {
    abstract fun getData(pos: Int): Short

    override fun get(x: Int, y: Int): Int = getData(dataPos(x, y)).toInt()

    override fun doWithLine(y: Int, work: (Int, Int) -> Unit) {
        var x = 0
        var pos = dataPos(x, y)
        while (x < resolution + 1) {
            work(x, getData(pos).toInt())
            x++
            pos += 2
        }
    }

    protected fun dataPos(x: Int, y: Int) = (x + (resolution + 1) * y) * 2
}

internal class BufferTile(val buffer: ByteBuffer, resolution: Int) : Tile(resolution) {
    override fun getData(pos: Int): Short = buffer.getShort(pos)

    fun write(value: PixelSource) {
        var y = 0
        while (y < resolution + 1) {
            value.doWithLine(y) { x, pixel ->
                val pos = dataPos(x, y)
                buffer.putShort(pos, pixel.toShort())
            }
            y += 1
        }
    }
}

internal class FileTile(val raf: RandomAccessFile, val start: Long, resolution: Int) : Tile(resolution) {
    override fun getData(pos: Int): Short {
        raf.seek(start + pos)
        return raf.readShort()
    }
}