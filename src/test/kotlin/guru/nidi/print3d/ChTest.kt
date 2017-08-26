package guru.nidi.print3d

import com.vividsolutions.jts.geom.*
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier
import guru.nidi.print3d.aster.AsterFile
import guru.nidi.print3d.aster.AsterZipPixelSourceProvider
import guru.nidi.print3d.csg.Vector
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
//                val env = Envelope(-180.0, 179.0, -83.0, 83.0)

                val file = File("target/$iso-$scale-${dh.toInt()}.stl")
                if (file.exists() && !overwrite) return

                StlBinaryWriter(file, iso).use { w ->
                    val aster = AsterFile(File("target"), scale, AsterZipPixelSourceProvider(File("/Volumes/MY-HD-1/aster")), true)
                    //                    val x = 2.110722 / 20 + (env.maxX + env.minX) / 2
//                    val y = -1.910370 / 20 + (env.maxY + env.minY) / 2
                    fun lng(v: Double) = (v - (env.maxX + env.minX) / 2) * 20

                    fun lat(v: Double) = (v - (env.maxY + env.minY) / 2) * 20
                    fun poin(x: Double, y: Double, z: Double) = Vector(lng(x), lat(y), z / dh)
                    fun point2(ll: LatLng, z: Double) = poin(ll.lng, ll.lat, z)
                    fun point2(ll: LatLng, z: Int) = poin(ll.lng, ll.lat, z.toDouble())
                    fun point(c: Coordinate, z: Double) = poin(c.x, c.y, z)
                    fun point(ll: LatLng) = point2(ll, aster.getPixel(ll).toDouble())

                    val da = LatLng(0.0, 0.0)
                    val db = LatLng(aster.coordStep, 0.0)
                    val dc = LatLng(0.0, aster.coordStep)
                    val dd = LatLng(aster.coordStep, aster.coordStep)

                    val simple = DouglasPeuckerSimplifier.simplify(geo, aster.coordStep)
                    val mp = geo as MultiPolygon
                    val s = geo.factory.createPolygon((geo.getGeometryN(0) as Polygon).exteriorRing.coordinates)

//                    for (i in 0 until mp.numGeometries) {
//                        val polygon = mp.getGeometryN(i) as Polygon
//                        val coord = polygon.exteriorRing.coordinates
//                        coord.forEachIndexed { i, c ->
//                            val next = (i + 1) % coord.size
//                            val a = LatLng(c.y, c.x)
//                            val b = LatLng(coord[next].y, coord[next].x)
//                            val ha = aster.getPixel(a).toDouble()
//                            val hb = aster.getPixel(b).toDouble()
//                            w.write(poin(a, ha), poin(b, 0.0), poin(a, 0.0))
//                            w.write(poin(a, ha), poin(b, hb), poin(b, 0.0))
//                        }
//
//                        triangulate(polygon.exteriorRing.coordinates.asList()).forEach {
//                            w.write(poin(it.p0, 0.0), poin(it.p1, 0.0), poin(it.p2, 0.0))
//                        }
//
//                    }

                    val ps = Array<IntArray>(((env.maxY - env.minY) / aster.coordStep).toInt() + 2) { _ ->
                        IntArray(((env.maxX - env.minX) / aster.coordStep).toInt() + 2) { _ ->
                            Int.MIN_VALUE
                        }
                    }
                    for (y in 0 until ps.size) {
                        println("$y of ${ps.size}")
                        for (x in 0 until ps[y].size) {
                            val p = LatLng(env.minY + y * aster.coordStep, env.minX + x * aster.coordStep)
                            if (simple.contains(geo.factory.createPoint(Coordinate(p.lng, p.lat)))) {
                                ps[y][x] = aster.getPixel(p)
                            }
                        }
                    }

                    val bottom = -1000
                    for (y in 0 until ps.size - 1) {
                        println("$y of ${ps.size}")
                        var wasIn = false
                        for (x in 0 until ps[y].size - 1) {
                            val a = ps[y][x]
                            val c = ps[y][x + 1]
                            val b = ps[y + 1][x]
                            val d = ps[y + 1][x + 1]
                            val isIn = a > Int.MIN_VALUE && b > Int.MIN_VALUE && c > Int.MIN_VALUE && d > Int.MIN_VALUE
                            val p = LatLng(env.minY + y * aster.coordStep, env.minX + x * aster.coordStep)
                            if (isIn) {
                                w.write(point2(p, a), point2(p + dd, d), point2(p + db, b))
                                w.write(point2(p, a), point2(p + dc, c), point2(p + dd, d))
                                w.write(point2(p, bottom), point2(p + db, bottom), point2(p + dd, bottom))
                                w.write(point2(p, bottom), point2(p + dd, bottom), point2(p + dc, bottom))
                                if (!wasIn) {
                                    w.write(point2(p, a), point2(p + db, b), point2(p + db, bottom))
                                    w.write(point2(p, a), point2(p + db, bottom), point2(p, bottom))
                                }
                            } else {
                                if (wasIn) {
                                    w.write(point2(p, a), point2(p + db, bottom), point2(p + db, b))
                                    w.write(point2(p, a), point2(p, bottom), point2(p + db, bottom))
                                }
                            }
                            wasIn = isIn
                        }
                    }

                    for (x in 0 until ps[0].size - 1) {
                        var wasIn = false
                        for (y in 0 until ps.size - 1) {
                            val a = ps[y][x]
                            val c = ps[y][x + 1]
                            val b = ps[y + 1][x]
                            val d = ps[y + 1][x + 1]
                            val isIn = a > Int.MIN_VALUE && b > Int.MIN_VALUE && c > Int.MIN_VALUE && d > Int.MIN_VALUE
                            val p = LatLng(env.minY + y * aster.coordStep, env.minX + x * aster.coordStep)
                            if (isIn) {
                                if (!wasIn) {
                                    w.write(point2(p, a), point2(p + dc, bottom), point2(p + dc, c))
                                    w.write(point2(p, a), point2(p, bottom), point2(p + dc, bottom))
                                }
                            } else {
                                if (wasIn) {
                                    w.write(point2(p, a), point2(p + dc, c), point2(p + dc, bottom))
                                    w.write(point2(p, a), point2(p + dc, bottom), point2(p, bottom))
                                }
                            }
                            wasIn = isIn
                        }
                    }

//                    var p = LatLng(env.minY, env.minX)
//                    while (p.lat < env.maxY) {
//                        p = p.copy(lng = env.minX)
//                        while (p.lng < env.maxX) {
//                            if (s.contains(geo.factory.createVector(Coordinate(p.lng, p.lat)))) {
//                                val pa = point(p + da)
//                                val pd = point(p + dd)
//                                w.write(pa, pd, point(p + db))
//                                w.write(pa, point(p + dc), pd)
//                                val i = ((p.lng - env.minX) / aster.coordStep).toInt()
//                            }
//                            p += LatLng(0.0, aster.coordStep)
//                        }
//                        p += LatLng(aster.coordStep, 0.0)
//                    }

                }
            }

            create("AT", 500.0, 1800, true)
//            for (s in listOf("CH", "DE", "FR", "IT", "LI", "AT", "BE", "NL", "DK", "ES", "PT", "SE", "FI", "NO", "GR", "RO", "BG", "SK", "CZ", "HU", "PL", "SI",
//                    "EE", "LV", "LT", "LU", "MD", "HR", "BY", "BA", "UA", "TR", "SJ", "GB", "GL", "IE", "IS", "AL", "RS"))
//                create(s, 3000.0, 144)
        }
    }


}