package guru.nidi.print3d.aster

import guru.nidi.print3d.PixelSource
import java.nio.ByteBuffer

internal class Tile(val buffer: ByteBuffer, val resolution: Int) : PixelSource {
    override fun get(x: Int, y: Int): Int = buffer.getShort(dataPos(x, y)).toInt()

    override fun doWithLine(y: Int, work: (Int, Int) -> Unit) {
        var x = 0
        var pos = dataPos(x, y)
        while (x < resolution + 1) {
            work(x, buffer.getShort(pos).toInt())
            x++
            pos += 2
        }
    }

    private fun dataPos(x: Int, y: Int) = (x + (resolution + 1) * y) * 2
}