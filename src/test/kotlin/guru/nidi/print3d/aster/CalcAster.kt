package guru.nidi.print3d.aster

import guru.nidi.print3d.LatLng
import io.kotlintest.specs.StringSpec
import java.io.File

class CalcAster : StringSpec() {
    init {
        "load" {
            val basedir = File("/Volumes/MY-HD-1/aster")
            val base = AsterFile(basedir, 1, null, true)
            val asters = listOf(
                    AsterFile(File("target"), 144, AsterPixelSourceProvider(base), false, updateOnly = true),
                    AsterFile(File("target"), 72, AsterPixelSourceProvider(base), false, updateOnly = true),
                    AsterFile(File("target"), 36, AsterPixelSourceProvider(base), false, updateOnly = true),
                    AsterFile(File("target"), 12, AsterPixelSourceProvider(base), false, updateOnly = true),
                    AsterFile(File("target"), 16, AsterPixelSourceProvider(base), false, updateOnly = true),
                    AsterFile(File("target"), 18, AsterPixelSourceProvider(base), false, updateOnly = true),
                    AsterFile(File("target"), 9, AsterPixelSourceProvider(base), false, updateOnly = true),
                    AsterFile(File("target"), 4, AsterPixelSourceProvider(base), false, updateOnly = true),
                    AsterFile(File("target"), 3, AsterPixelSourceProvider(base), false, updateOnly = true),
                    AsterFile(File("target"), 2, AsterPixelSourceProvider(base), false, updateOnly = true),
                    AsterFile(File("target"), 5, AsterPixelSourceProvider(base), false, updateOnly = true),
                    AsterFile(File("target"), 6, AsterPixelSourceProvider(base), false, updateOnly = true))

            for (lat in -83..82) {
                for (lng in -180..179) {
                    val p = LatLng(lat.toDouble(), lng.toDouble())
                    println(p)
                    asters.forEach { it.getPixel(p) }
                }
            }
        }
    }
}