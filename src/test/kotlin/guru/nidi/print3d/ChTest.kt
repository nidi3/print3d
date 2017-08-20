package guru.nidi.print3d

import com.vividsolutions.jts.geom.*
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder
import guru.nidi.print3d.aster.AsterFile
import guru.nidi.print3d.aster.AsterZipPixelSourceProvider
import guru.nidi.print3d.data.Point
import guru.nidi.print3d.data.StlBinaryWriter
import io.kotlintest.specs.StringSpec
import org.geotools.data.FileDataStoreFinder
import org.geotools.filter.text.cql2.CQL
import java.io.File


class ChTest : StringSpec() {
    init {
        "load" {
            fun create(iso: String, dh: Double, scale: Int, overwrite: Boolean = false) {
                val store = FileDataStoreFinder.getDataStore(File("src/main/resources/TM_WORLD_BORDERS-0.3/TM_WORLD_BORDERS-0.3.shp"))
                val featureSource = store.featureSource
                val features = featureSource.getFeatures(CQL.toFilter("ISO2 = '$iso'")).features()
                val ch = features.next()
                val geo = ch.defaultGeometryProperty.value as Geometry
                val env = geo.envelopeInternal

                val file = File("target/$iso-$scale-${dh.toInt()}.stl")
                if (file.exists() && !overwrite) return

                StlBinaryWriter(file, iso).use { w ->
                    fun lng(v: Double) = (v - 6) * 10
                    fun lat(v: Double) = (v - 45) * 10
                    fun point(x: Double, y: Double, z: Double) = Point(lng(x), lat(y), z / dh)


                    val aster = AsterFile(File("target"), scale, AsterZipPixelSourceProvider(File("/Volumes/MY-HD-1/aster")), true)
                    val mp = geo as MultiPolygon
                    for (i in 0 until mp.numGeometries) {
                        val polygon = mp.getGeometryN(i) as Polygon
                        val coord = polygon.exteriorRing.coordinates
                        coord.forEachIndexed { i, c ->
                            val next = (i + 1) % coord.size
                            val a = LatLng(c.y, c.x)
                            val b = LatLng(coord[next].y, coord[next].x)
                            val h = aster.getPixel(a)
                            val h2 = aster.getPixel(b)
                            w.writeTriangle(point(a.lng, a.lat, h.toDouble()), point(a.lng, a.lat, 0.0), point(b.lng, b.lat, 0.0))
                            w.writeTriangle(point(a.lng, a.lat, h.toDouble()), point(b.lng, b.lat, h2.toDouble()), point(b.lng, b.lat, 0.0))
                        }
                        DelaunayTriangulationBuilder().apply {
                            setSites(polygon.exteriorRing)
                            val triangles = getTriangles(geo.factory) as GeometryCollection
                            for (i in 0 until triangles.numGeometries) {
                                val cs = triangles.getGeometryN(i).coordinates
                                w.writeTriangle(point(cs[0].x, cs[0].y, i-1000.0), point(cs[1].x, cs[1].y, i-1000.0), point(cs[2].x, cs[2].y,i-1000.0))
                            }
                        }
                    }

/*
                    var lat = env.minY
                    while (lat < env.maxY) {
                        var lng = env.minX
                        while (lng < env.maxX) {
                            if (geo.contains(geo.factory.createPoint(Coordinate(lng, lat)))) {
                                val a = aster.getPixel(LatLng(lat, lng)).toDouble()
                                val b = aster.getPixel(LatLng(lat + aster.coordStep, lng)).toDouble()
                                val c = aster.getPixel(LatLng(lat, lng + aster.coordStep)).toDouble()
                                val d = aster.getPixel(LatLng(lat + aster.coordStep, lng + aster.coordStep)).toDouble()
                                val pa = point(lng, lat, a)
                                val pd = point(lng + aster.coordStep, lat + aster.coordStep, d)
                                w.writeTriangle(pa, point(lng, lat + aster.coordStep, b), pd)
                                w.writeTriangle(pa, point(lng + aster.coordStep, lat, c), pd)
                            }
                            lng += aster.coordStep
                        }
                        lat += aster.coordStep
                    }*/
                }
            }

            create("CH", 3000.0, 144, true)
//            for (s in listOf("CH", "DE", "FR", "IT", "LI", "AT", "BE", "NL", "DK", "ES", "PT", "SE", "FI", "NO", "GR", "RO", "BG", "SK", "CZ", "HU", "PL", "SI",
//                    "EE", "LV", "LT", "LU", "MD", "HR", "BY", "BA", "UA", "TR", "SJ", "GB", "GL", "IE", "IS", "AL", "RS"))
//                create(s, 3000.0, 144)
        }
    }

}