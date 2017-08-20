package guru.nidi.print3d.data

import java.io.*

class StlBinaryWriter(val file: File, val name: String) : AutoCloseable {
    private val out = DataOutputStream(FileOutputStream(file))
    private var count = 0

    init {
        out.write(ByteArray(80))
        out.writeInt(0)
    }

    fun writeTriangle(a: Point, b: Point, c: Point) {
        write(0.0)
        write(0.0)
        write(0.0)
        write(a.x)
        write(a.y)
        write(a.z)
        write(b.x)
        write(b.y)
        write(b.z)
        write(c.x)
        write(c.y)
        write(c.z)
        out.writeShort(0)
        count++
    }

    private fun write(v: Double) {
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