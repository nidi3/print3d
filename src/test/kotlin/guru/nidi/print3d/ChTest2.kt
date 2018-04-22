package guru.nidi.print3d

import guru.nidi.print3d.aster.AsterFile
import guru.nidi.print3d.aster.AsterZipPixelSourceProvider
import guru.nidi.print3d.csg.*
import io.kotlintest.specs.StringSpec
import java.io.File


class ChTest2 : StringSpec() {
    init {
        "load" {
            fun create(steps: Int, dh: Double, scale: Int, overwrite: Boolean = false) {
                val file = File("target/$steps-$scale-${dh.toInt()}.stl")
                if (file.exists() && !overwrite) return

                val aster = AsterFile(File("target"), scale, AsterZipPixelSourceProvider(File("/Volumes/MY-HD-1/aster")), true)
                fun height(lat: Double, lng: Double): Int {
                    val a = (lat + 270) % 180 - 90
                    val n = (lng + 540) % 360 - 180
                    return aster.getPixel(LatLng(a, n))
                }

                val sea = 0.0

                Model().apply {
                    addToModel(sphere(center = Vector(0.0, 0.0, 0.0), slices = steps, stacks = steps / 2, radiusFunc = { phi, theta ->
                        val lat = theta * 180 - 90
                        val lng = (if (phi == 1.0) 0.0 else phi) * 360 - 180
                        if (lat > -80 && lat < 80) {
                            println("" + lat + " " + lng)
                            val h = (4 * aster.getPixel(LatLng(lat, lng))
                                    + height(lat - 180 / steps, lng)
                                    + height(lat + 180 / steps, lng)
                                    + height(lat, lng - 180 / steps)
                                    + height(lat, lng + 180 / steps)) / 8.0
                            20 + (if (h < -0) -sea else h) / dh
                        } else {
                            20.0 - sea / dh
                        }
                    }))
                    write(file, "world")
                }
            }
            create(1024, 2000.0, 450, true)
        }
    }


}