package guru.nidi.print3d.aster

import guru.nidi.print3d.PixelSourceProvider
import guru.nidi.print3d.geotiff.GeoTiff
import guru.nidi.print3d.geotiff.GeoTiffReader
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile


class AsterZipPixelSourceProvider(val basedir: File) : PixelSourceProvider {
    override fun sourceFor(lat: Int, lng: Int): GeoTiff? {
        val lngStr = if (lng < 0) "W" + "%03d".format(-lng) else "E" + "%03d".format(lng)
        val latStr = if (lat < 0) "S" + "%02d".format(-lat) else "N" + "%02d".format(lat)
        val name = "ASTGTM2_$latStr$lngStr"
        val zipFile = File(basedir, "$latStr/$name.zip")

        return if (!zipFile.exists()) null
        else unzip(zipFile).let { (inp, size) ->
            GeoTiffReader.read(inp, size)
        }
    }

    private fun unzip(zip: File): Pair<InputStream, Int> {
        val zipFile = ZipFile(zip)
        return zipFile.entries().asSequence()
                .find { e -> e.name.endsWith("_dem.tif") }
                .let { e -> Pair(zipFile.getInputStream(e), e!!.size.toInt()) }
    }
}
