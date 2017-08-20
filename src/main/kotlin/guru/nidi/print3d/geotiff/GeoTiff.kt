package guru.nidi.print3d.geotiff

import guru.nidi.print3d.PixelSource

class GeoTiff(val reader: GeoTiffReader,
              val width: Int, val height: Int,
              val bitsPerSample: Int, val info: Map<String, String>,
              val xResolution: Double, val yResolution: Double, val resolutionUnit: Int,
              val modelPixelScale: DoubleArray,
              val modelTiepoints: DoubleArray?,
              val geoKeyDirectory: IntArray?) : PixelSource {

  override fun get(x: Int, y: Int): Int = reader.getPixel(x, y)

  override fun doWithLine(y: Int, work: (Int, Int) -> Unit) = reader.doWithPixels(y, work)
}