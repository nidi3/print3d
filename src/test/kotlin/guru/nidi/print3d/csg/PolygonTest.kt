package guru.nidi.print3d.csg

import io.kotlintest.specs.StringSpec
import java.io.File

class PolygonTest : StringSpec() {
    init {
//        "flip" {
//            val a = Vector(1.0, 2.0, 3.0)
//            val b = Vector(1.0, 2.0, 3.0)
//            val c = Vector(1.0, 2.0, 3.0)
//            Polygon(Vertex(a, a), Vertex(b, b), Vertex(c, c)).flip() shouldEqual
//                    Polygon(Vertex(c, -c), Vertex(b, -b), Vertex(a, -a))
//        }
//        "rupert" {
//            model {
//
//                transform(scale(v(10, 10, 10))) {
//                    val s = 1.5 * sqrt(2.0) - 2
//                    val q = cube(2.0 * unit, 2.0 * unit)
//                    val a = v(1 + s, 4 - s, 4)
//                    val b = v(4 - s, 1 + s, 4)
//                    val c = v(3 - s, 0 + s, 0)
//                    val d = v(0 + s, 3 - s, 0)
//                    val n = -4.0 * Plane.fromPoints(a, b, c).normal
//                    addToModel(q - convexPrism(8.0, a + n, b + n, c + n, d + n))
//                    addToModel(q intersect convexPrism(8.0, a + n, b + n, c + n, d + n))
//                    addToModel(q)
//                }
//                write(File("target/rupert.stl"), "rupert")
//            }
//        }
        "fix"{
            model {
                val base = convexPrism(30.0, v(-2, 0, 0), v(-4, 0, 4), v(4, 0, 4), v(2, 0, 0))
//                val bigConn = conn.scale(1.2 * unit)
                val conn = base.rotateZ(45.deg())
                val bigConn = base.growLinear(v(1, 1, 0)).translate(v(0, 0, 0)).rotateZ(45.deg())

//                add(conn)
//                add(bigConn)

                add(cube(v(0, 0, 3), v(10, 10, 3)))
                add(conn.translate(v(-15, -5, 6)) and cube(origin, v(10, 10, 10)))
                add(conn.translate(v(-5, -15, 6)) and cube(origin, v(10, 10, 10)))

                val neg = cube(v(30, 10, 0), v(10, 10, 5)) - bigConn.translate(v(15, 5, -5)) - bigConn.translate(v(25, -5, -5))
                add(neg.rotateX(180.deg()).translate(v(0, 0, 5)))

                write(File("target/fix.stl"), "fix")
            }
        }
    }
}