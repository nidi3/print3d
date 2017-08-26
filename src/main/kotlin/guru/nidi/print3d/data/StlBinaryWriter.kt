package guru.nidi.print3d.data

import guru.nidi.print3d.csg.*
import java.io.*
import java.lang.Math.abs

class StlBinaryWriter(val file: File, val name: String) : AutoCloseable {
    private val out = DataOutputStream(FileOutputStream(file))
    private var count = 0

    init {
        out.write(ByteArray(80))
        out.writeInt(0)
    }

    fun write(t: Csg, normals: Boolean = false) = write(t.polygons, normals)

    fun write(t: Polygon, normals: Boolean = false) {
        write(t.vertices[0].pos, t.vertices[1].pos, t.vertices[2].pos)
        if (normals) {
            t.vertices.forEach { v ->
                val n = v.normal.unit()
                val p = when {
                    abs(n.y) > .2 -> Vector(0.0, -n.z, -n.y)
                    abs(n.z) > .2 -> Vector(-n.z, 0.0, -n.x)
                    abs(n.x) > .2 -> Vector(-n.y, -n.x, 0.0)
                    else -> Vector(0.0, 0.0, 0.0)
                }
                write(v.pos, v.pos + n, v.pos + p)
            }
        }
    }

    fun write(t: List<Polygon>, normals: Boolean = false) = t.map { write(it, normals) }

    fun write(a: Vector, b: Vector, c: Vector) {
        wr(0.0)
        wr(0.0)
        wr(0.0)
        wr(a)
        wr(b)
        wr(c)
        out.writeShort(0)
        count++
    }

    private fun wr(a: Vector) {
        wr(a.x)
        wr(a.y)
        wr(a.z)
    }

    private fun wr(v: Double) {
        val i = java.lang.Float.floatToIntBits(v.toFloat())
        out.writeInt(java.lang.Integer.reverseBytes(i))
    }

    override fun close() {
        out.close()
        RandomAccessFile(file, "rw").use {
            it.seek(80)
            it.writeInt(java.lang.Integer.reverseBytes(count))
        }
    }
}