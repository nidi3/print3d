package guru.nidi.print3d.csg

import guru.nidi.print3d.data.StlBinaryWriter
import java.io.File

class Model {
    private val csgs = mutableListOf<Csg>()
    private var transform = AffineTransform()

    fun <T> transform(a: AffineTransform, block: Model.() -> T): T {
        val orig = transform
        try {
            transform = a.applyTo(transform)
            return block()
        } finally {
            transform = orig
        }
    }

    val origin = v(0, 0, 0)
    val unit = v(1, 1, 1)

    fun v(a: Double, b: Double, c: Double) = Vector(a, b, c)
    fun v(a: Int, b: Double, c: Double) = Vector(a.toDouble(), b, c)
    fun v(a: Double, b: Int, c: Double) = Vector(a, b.toDouble(), c)
    fun v(a: Double, b: Double, c: Int) = Vector(a, b, c.toDouble())
    fun v(a: Int, b: Int, c: Double) = Vector(a.toDouble(), b.toDouble(), c)
    fun v(a: Int, b: Double, c: Int) = Vector(a.toDouble(), b, c.toDouble())
    fun v(a: Double, b: Int, c: Int) = Vector(a, b.toDouble(), c.toDouble())
    fun v(a: Int, b: Int, c: Int) = Vector(a.toDouble(), b.toDouble(), c.toDouble())

    fun translate(v: Vector) = AffineTransform().translate(v)
    fun scale(v: Vector) = AffineTransform().scale(v)
    fun rotateX(a: Double) = AffineTransform().rotateX(a)
    fun rotateY(a: Double) = AffineTransform().rotateY(a)
    fun rotateZ(a: Double) = AffineTransform().rotateZ(a)

    fun Csg.translate(v: Vector) = AffineTransform().translate(v).applyTo(this)
    fun Csg.scale(v: Vector) = AffineTransform().scale(v).applyTo(this)
    fun Csg.rotateX(a: Double) = AffineTransform().rotateX(a).applyTo(this)
    fun Csg.rotateY(a: Double) = AffineTransform().rotateY(a).applyTo(this)
    fun Csg.rotateZ(a: Double) = AffineTransform().rotateZ(a).applyTo(this)

    fun addToModel(csg: Csg) = csgs.add(transform.applyTo(csg))

    fun write(f: File, name: String) {
        StlBinaryWriter(f, name).use { out ->
            csgs.forEach { c ->
                c.polygons.forEach { p ->
                    p.toTriangles().forEach { t ->
                        out.write(t)
                    }
                }
            }

        }
    }

}
