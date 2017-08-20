package guru.nidi.print3d.aster

import guru.nidi.print3d.LatLng
import io.kotlintest.specs.StringSpec
import java.io.File

class AsterTest : StringSpec() {
    init {
        "load" {
            val basedir = File("/Volumes/MY-HD-1/aster")
            val aster = AsterFile(File("target"), 36, AsterZipPixelSourceProvider(basedir), true)
            aster.getPixel(LatLng(46.0, 7.0))
        }

    }
}