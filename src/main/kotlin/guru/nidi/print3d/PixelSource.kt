package guru.nidi.print3d

interface PixelSource {
    operator fun get(x: Int, y: Int): Int

    fun doWithLine(y: Int, work: (Int, Int) -> Unit)
}



