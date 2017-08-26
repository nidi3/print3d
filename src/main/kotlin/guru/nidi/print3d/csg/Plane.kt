package guru.nidi.print3d.csg

import guru.nidi.print3d.csg.Plane.Type.*

data class Plane private constructor(private val normal: Vector, private val w: Double) {
    private enum class Type(val i: Int) {
        COPLANAR(0), FRONT(1), BACK(2), SPANNING(3);

        infix fun or(t: Type) = values().find { it.i == i or t.i }!!
    }

    private val EPSILON = 1e-5

    companion object {
        fun fromPoints(a: Vector, b: Vector, c: Vector): Plane {
            val n = ((b - a) cross (c - a)).unit()
            return Plane(n, n dot a)
        }
    }

    fun flip() = Plane(-normal, -w)

    fun splitPolygon(polygon: Polygon,
                     coplanarFront: MutableList<Polygon>, coplanarBack: MutableList<Polygon>,
                     front: MutableList<Polygon>, back: MutableList<Polygon>) {
        var polygonType = COPLANAR
        val types = polygon.vertices.map { v ->
            val t = (normal dot v.pos) - w
            val type = when {
                (t < -EPSILON) -> BACK
                (t > EPSILON) -> FRONT
                else -> COPLANAR
            }
            polygonType = polygonType or type
            type
        }

        when (polygonType) {
            COPLANAR -> {
                val co = if (normal dot polygon.plane.normal > 0) coplanarFront else coplanarBack
                co.add(polygon)
            }
            FRONT -> front.add(polygon)
            BACK -> back.add(polygon)
            SPANNING -> {
                val f = mutableListOf<Vertex>()
                val b = mutableListOf<Vertex>()
                polygon.vertices.forEachIndexed { i, _ ->
                    val j = (i + 1) % polygon.vertices.size
                    val ti = types[i]
                    val tj = types[j]
                    val vi = polygon.vertices[i]
                    val vj = polygon.vertices[j]
                    if (ti != BACK) f.add(vi)
                    if (ti != FRONT) b.add(vi)
                    if (ti or tj == SPANNING) {
                        val t = (w - (normal dot vi.pos)) / (normal dot (vj.pos - vi.pos))
                        val v = vi.interpolate(vj, t)
                        f.add(v)
                        b.add(v)
                    }
                }
                if (f.size >= 3) front.add(Polygon(f, polygon.props))
                if (b.size >= 3) back.add(Polygon(b, polygon.props))
            }
        }
    }
}