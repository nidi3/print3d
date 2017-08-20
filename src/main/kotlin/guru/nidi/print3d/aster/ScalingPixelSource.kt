package guru.nidi.print3d.aster

import guru.nidi.print3d.PixelSource

class ScalingPixelSource(val source: PixelSource, val scale: Int) : PixelSource {
    override fun get(x: Int, y: Int): Int = source[x * scale, y * scale]

    override fun doWithLine(y: Int, work: (Int, Int) -> Unit) {
        source.doWithLine(y * scale) { x, p ->
            if (x % scale == 0) work(x / scale, p)
        }
    }
}