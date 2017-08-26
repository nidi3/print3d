package guru.nidi.print3d.csg

import java.lang.Math.*

class Csg(val polygons: List<Polygon>) {
    fun union(csg: Csg): Csg {
        val b = Node(csg.polygons)
        val a = Node(polygons).clipTo(b)
        val b2 = b.clipTo(a).invert().clipTo(a).invert()
        return Csg(a.combine(b2).allPolygons())
    }

    fun subtract(csg: Csg): Csg {
        val b = Node(csg.polygons)
        val a = Node(polygons).invert().clipTo(b)
        val b1 = b.clipTo(a).invert().clipTo(a).invert()
        return Csg(a.combine(b1).invert().allPolygons())
    }

    fun intersect(csg: Csg): Csg {
        val a = Node(polygons).invert()
        val b = Node(csg.polygons).clipTo(a).invert()
        val a1 = a.clipTo(b)
        val b2 = b.clipTo(a1)
        return Csg(a1.combine(b2).invert().allPolygons())
    }

    fun inverse() = Csg(polygons.map { it.flip() })
}

fun cube(center: Vector = Vector(0.0, 0.0, 0.0), radius: Vector = Vector(1.0, 1.0, 1.0),
         props: Map<String, *> = mapOf<String, String>()): Csg {
    return Csg(listOf(
            listOf(listOf(0, 4, 6, 2), listOf(-1, 0, 0)),
            listOf(listOf(1, 3, 7, 5), listOf(+1, 0, 0)),
            listOf(listOf(0, 1, 5, 4), listOf(0, -1, 0)),
            listOf(listOf(2, 6, 7, 3), listOf(0, +1, 0)),
            listOf(listOf(0, 2, 3, 1), listOf(0, 0, -1)),
            listOf(listOf(4, 5, 7, 6), listOf(0, 0, +1)))
            .map { info ->
                Polygon(info[0].map { i ->
                    val pos = Vector(
                            center.x + radius.x * (2 * (i and 1) - 1),
                            center.y + radius.y * (2 * ((i shr 1) and 1) - 1),
                            center.z + radius.z * (2 * ((i shr 2) and 1) - 1)
                    )
                    Vertex(pos, Vector(info[1][0].toDouble(), info[1][1].toDouble(), info[1][2].toDouble()))
                }, props)
            })
}

fun sphere(center: Vector = Vector(0.0, 0.0, 0.0), radius: Double = 1.0, slices: Int = 32, stacks: Int = 16,
           props: Map<String, *> = mapOf<String, String>()): Csg {
    fun vertex(th: Double, ph: Double): Vertex {
        val theta = th * PI * 2;
        val phi = ph * PI;
        val dir = Vector(cos(theta) * sin(phi), cos(phi), sin(theta) * sin(phi))
        return Vertex(center + dir * radius, dir)
    }

    val polygons = mutableListOf<Polygon>()
    for (i in 0 until slices) {
        for (j in 0 until stacks) {
            val vertices = mutableListOf<Vertex>()
            val id = i.toDouble()
            val jd = j.toDouble()
            vertices.add(vertex(id / slices, jd / stacks))
            if (j > 0) vertices.add(vertex((id + 1) / slices, jd / stacks))
            if (j < stacks - 1) vertices.add(vertex((id + 1) / slices, (jd + 1) / stacks))
            vertices.add(vertex(id / slices, (jd + 1) / stacks))
            polygons.add(Polygon(vertices, props))
        }
    }
    return Csg(polygons)
}

fun cylinder(start: Vector = Vector(0.0, -1.0, 0.0), end: Vector = Vector(0.0, 1.0, 0.0), radius: Double = 1.0,
             slices: Int = 32, props: Map<String, *> = mapOf<String, String>()): Csg {
    val ray = end - start
    val axisZ = ray.unit()
    val isY = if (abs(axisZ.y) > 0.5) 1.0 else 0.0
    val axisX = (Vector(isY, 1.0 - isY, 0.0) cross axisZ).unit()
    val axisY = (axisX cross axisZ).unit()
    val s = Vertex(start, -axisZ)
    val e = Vertex(end, axisZ.unit())
    fun point(stack: Double, slice: Double, normalBlend: Double): Vertex {
        val angle = slice * PI * 2;
        val out = axisX * cos(angle) + axisY * sin(angle)
        val pos = start + ray * stack + out * radius
        val normal = out * (1 - abs(normalBlend)) + axisZ * normalBlend
        return Vertex(pos, normal)
    }

    val polygons = mutableListOf<Polygon>()
    for (i in 0 until slices) {
        val id = i.toDouble()
        val t0 = id / slices
        val t1 = (id + 1) / slices
        polygons.add(Polygon(props, s, point(0.0, t0, -1.0), point(0.0, t1, -1.0)))
        polygons.add(Polygon(props, point(0.0, t1, 0.0), point(0.0, t0, 0.0), point(1.0, t0, 0.0), point(1.0, t1, 0.0)))
        polygons.add(Polygon(props, e, point(1.0, t1, 1.0), point(1.0, t0, 1.0)))
    }
    return Csg(polygons)
}

fun ring(r1: Double, r2: Double, h: Double, slices: Int = 32): Csg {
    fun vertex(r: Double, a: Double, b: Double, norm: Vector) = Vertex(Vector(r * sin(b) * cos(a), r * sin(b) * sin(a), r * cos(b)), norm)
    fun vertex(r: Double, a: Double, b: Double, dir: Int): Vertex {
        val v = Vector(r * sin(b) * cos(a), r * sin(b) * sin(a), r * cos(b))
        return Vertex(v, v * dir.toDouble())
    }

    val res = mutableListOf<Polygon>()
    val da = 2 * PI / slices
    var a = 0.0
    val down = Vector(0.0, 0.0, 1.0)
    while (a < 2 * PI + da / 2) {
        var b = (PI - h / r2) / 2
        res.add(Polygon(vertex(r1, a, b, down), vertex(r2, a, b,down), vertex(r2, a + da, b, down), vertex(r1, a + da, b, down)))
        val db = h / r2 //todo use delta
        while (b < (PI + h / r2) / 2) {
            res.add(Polygon(vertex(r1, a, b, -1), vertex(r1, a + da, b, -1), vertex(r1, a + da, b + db, -1), vertex(r1, a, b + db, -1)))
            res.add(Polygon(vertex(r2, a, b, 1), vertex(r2, a, b + db, 1), vertex(r2, a + da, b + db, 1), vertex(r2, a + da, b, 1)))
            b += db
        }
        res.add(Polygon(vertex(r1, a, b, -down), vertex(r1, a + da, b, -down), vertex(r2, a + da, b, -down), vertex(r2, a, b, -down)))
        a += da
    }
    return Csg(res)
}