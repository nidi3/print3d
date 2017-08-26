package guru.nidi.print3d.data

import guru.nidi.print3d.csg.Vector
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter


object PlyWriter {
    fun write(file: File, points: List<Vector>) {
        PrintWriter(OutputStreamWriter(FileOutputStream(file))).use { out ->
            out.println("ply\nformat ascii 1.0\nelement vertex ${points.size}\n" +
                    "property float x\nproperty float y\nproperty float z\nend_header\n")
            points.forEach { out.println("${it.x} ${it.y} ${it.z}") }
        }
    }
}

