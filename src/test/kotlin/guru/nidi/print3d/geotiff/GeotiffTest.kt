package guru.nidi.print3d.geotiff

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.File
import java.util.zip.ZipFile

class GeotiffTest : StringSpec() {
    init {
        "load" {
            val zip = ZipFile(File("src/test/resources/ASTGTM2_S78E050.zip"))
            val data = zip.entries().asSequence().find {
                it.name.endsWith("ASTGTM2_S78E050_dem.tif")
            }!!
            val tiff = GeoTiffReader.read(zip.getInputStream(data), data.size.toInt())
            tiff.width shouldBe 3601
            tiff.height shouldBe 3601
            tiff[0, 0] shouldBe 3520
            tiff.doWithLine(0) { x, p ->
                println("$x $p")
            }
        }

    }
}