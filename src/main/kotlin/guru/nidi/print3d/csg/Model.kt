package guru.nidi.print3d.csg

import guru.nidi.print3d.data.StlBinaryWriter
import java.io.File
import kotlin.math.PI

class Model {
    private val csgs = mutableListOf<Csg>()
    private var transform = AffineTransform()

    fun <T> transform(a: AffineTransform, block: Model.() -> T): T {
        val orig = transform
        try {
            transform = transform.applyTo(a)
            return block()
        } finally {
            transform = orig
        }
    }

    fun add(csg: Csg) = csgs.add(transform.applyTo(csg))

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

fun model(actions: Model.() -> Unit) = Model().apply(actions)
fun Int.deg() = this * PI / 180
fun Double.deg() = this * PI / 180
fun Double.rad() = this