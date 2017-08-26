package guru.nidi.print3d.data

import guru.nidi.print3d.csg.Vector
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter

class StlWriter(file: File, val name: String) : AutoCloseable {
    private val out = PrintWriter(OutputStreamWriter(FileOutputStream(file)))

    init {
        out.println("solid $name")
    }

    fun writeTriangle(a: Vector, b: Vector, c: Vector) {
        out.println("facet normal 0 0 0")
        out.println("outer loop")
        out.println("vertex ${a.x} ${a.y} ${a.z}")
        out.println("vertex ${b.x} ${b.y} ${b.z}")
        out.println("vertex ${c.x} ${c.y} ${c.z}")
        out.println("endloop")
        out.println("endfacet")
    }

    override fun close() {
        out.println("endsolid $name")
        out.close()
    }
}
