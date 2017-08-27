package guru.nidi.print3d.csg

class AffineTransform private constructor(val v00: Double, val v01: Double, val v02: Double, val v03: Double,
                                          val v10: Double, val v11: Double, val v12: Double, val v13: Double,
                                          val v20: Double, val v21: Double, val v22: Double, val v23: Double) {
    constructor() : this(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0)

    fun translate(v: Vector) = AffineTransform(
            v00, v01, v02, v03 + v.x,
            v10, v11, v12, v13 + v.y,
            v20, v21, v22, v23 + v.z)

    fun scale(v: Vector) = AffineTransform(
            v00 * v.x, v01, v02, v03,
            v10, v11 * v.y, v12, v13,
            v20, v21, v22 * v.z, v23)

    fun rotateX(a: Double) = AffineTransform(
            v00, v01 * Math.cos(a) + v02 * Math.sin(a), -v01 * Math.sin(a) + v02 * Math.cos(a), v03,
            v10, v11 * Math.cos(a) + v12 * Math.sin(a), -v11 * Math.sin(a) + v12 * Math.cos(a), v13,
            v20, v21 * Math.cos(a) + v22 * Math.sin(a), -v21 * Math.sin(a) + v22 * Math.cos(a), v23)

    fun rotateY(a: Double) = AffineTransform(
            v00 * Math.cos(a) + v02 * Math.sin(a), v01, -v00 * Math.sin(a) + v02 * Math.cos(a), v03,
            v10 * Math.cos(a) + v12 * Math.sin(a), v11, -v10 * Math.sin(a) + v12 * Math.cos(a), v13,
            v20 * Math.cos(a) + v22 * Math.sin(a), v21, -v20 * Math.sin(a) + v22 * Math.cos(a), v23)

    fun rotateZ(a: Double) = AffineTransform(
            v00 * Math.cos(a) - v01 * Math.sin(a), v00 * Math.sin(a) + v01 * Math.cos(a), v02, v03,
            v10 * Math.cos(a) - v11 * Math.sin(a), v10 * Math.sin(a) + v11 * Math.cos(a), v12, v13,
            v20 * Math.cos(a) - v21 * Math.sin(a), v20 * Math.sin(a) + v21 * Math.cos(a), v22, v23)

    fun applyTo(m: AffineTransform) = AffineTransform(
            v00 * m.v00 + v01 * m.v10 + v02 * m.v20, v00 * m.v10 + v01 * m.v11 + v02 * m.v21, v00 * m.v20 + v01 * m.v21 + v02 * m.v22, v03,
            v10 * m.v00 + v11 * m.v10 + v12 * m.v20, v10 * m.v10 + v11 * m.v11 + v12 * m.v21, v10 * m.v20 + v11 * m.v21 + v12 * m.v22, v13,
            v20 * m.v00 + v21 * m.v10 + v22 * m.v20, v20 * m.v10 + v21 * m.v11 + v22 * m.v21, v20 * m.v20 + v21 * m.v21 + v22 * m.v22, v23)

    fun applyTo(p: Vector) = Vector(
            v00 * p.x + v01 * p.y + v02 * p.z + v03,
            v10 * p.x + v11 * p.y + v12 * p.z + v13,
            v20 * p.x + v21 * p.y + v22 * p.z + v23)

    fun applyTo(t: Vertex) = Vertex(applyTo(t.pos), applyTo(t.normal))

    fun applyTo(t: Polygon) = Polygon(t.vertices.map { applyTo(it) }, t.props)

    fun applyTo(t: Csg) = Csg(t.polygons.map { applyTo(it) })
}