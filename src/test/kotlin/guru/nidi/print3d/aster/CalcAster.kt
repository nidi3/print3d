package guru.nidi.print3d.aster

import guru.nidi.print3d.LatLng
import io.kotlintest.specs.StringSpec
import java.io.File

class CalcAster : StringSpec() {
    init {
        "load" {
            val aster = AsterFile(File("target"), 144, AsterZipPixelSourceProvider(File("/Volumes/MY-HD-1/aster")), true)
            for (lat in -83..82) {
                for (lng in -180..179) {
                    println("$lat $lng")
                    aster.getPixel(LatLng(lat.toDouble(), lng.toDouble()))
                }
            }
        }
    }
}