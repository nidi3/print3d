package guru.nidi.print3d.csg

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class PlaneTest : StringSpec() {
    init {
        "splitPolygon simple cases" {
            val xyPlane = Plane.fromPoints(Vector(0.0, 0.0, 0.0), Vector(1.0, 0.0, 0.0), Vector(0.0, 1.0, 0.0))
            val n = Vector(0.0, 0.0, 1.0)
            val high = Polygon(Vertex(Vector(0.0, 0.0, 1.0), n), Vertex(Vector(1.0, 0.0, 1.0), n), Vertex(Vector(0.0, 1.0, 1.0), n))
            val low = Polygon(Vertex(Vector(0.0, 0.0, -1.0), n), Vertex(Vector(1.0, 0.0, -1.0), n), Vertex(Vector(0.0, 1.0, -1.0), n))
            val zero = Polygon(Vertex(Vector(0.0, 0.0, 0.0), n), Vertex(Vector(1.0, 0.0, 0.0), n), Vertex(Vector(0.0, 1.0, 0.0), n))
            val mzero = Polygon(Vertex(Vector(0.0, 0.0, 0.0), n), Vertex(Vector(0.0, 1.0, 0.0), n), Vertex(Vector(1.0, 0.0, 0.0), n))
            test(xyPlane, high, listOf(listOf(), listOf(), listOf(high), listOf()))
            test(xyPlane, low, listOf(listOf(), listOf(), listOf(), listOf(low)))
            test(xyPlane, zero, listOf(listOf(zero), listOf(), listOf(), listOf()))
            test(xyPlane, mzero, listOf(listOf(), listOf(mzero), listOf(), listOf()))
        }

        "splitPolygon"{
            val xyPlane = Plane.fromPoints(Vector(0.0, 0.0, 0.0), Vector(1.0, 0.0, 0.0), Vector(0.0, 1.0, 0.0))
            val n = Vector(0.0, 0.0, 1.0)
            val p = Polygon(Vertex(Vector(0.0, 0.0, 1.0), n), Vertex(Vector(1.0, 0.0, 1.0), n), Vertex(Vector(0.0, 1.0, -1.0), n))
            val up = Polygon(Vertex(Vector(0.0, 0.0, 1.0), n), Vertex(Vector(1.0, 0.0, 1.0), n), Vertex(Vector(0.5, 0.5, 0.0), n), Vertex(Vector(0.0, 0.5, 0.0), n))
            val down = Polygon(Vertex(Vector(0.5, 0.5, 0.0), n), Vertex(Vector(0.0, 1.0, -1.0), n), Vertex(Vector(0.0, 0.5, 0.0), n))
            test(xyPlane, p, listOf(listOf(), listOf(), listOf(up), listOf(down)))
        }
    }

    fun test(p: Plane, po: Polygon, expected: List<List<Polygon>>) {
        val res = listOf<MutableList<Polygon>>(mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())
        p.splitPolygon(po, res[0], res[1], res[2], res[3])
        res.forEachIndexed { i, _ ->
            res[i] shouldBe expected[i]
        }
    }
}