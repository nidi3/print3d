package guru.nidi.print3d.csg

import guru.nidi.print3d.data.StlBinaryWriter
import io.kotlintest.specs.StringSpec
import java.io.File

class CsgTest : StringSpec() {
    init {
        "create" {
            StlBinaryWriter(File("target/csg.stl"), "csg").use { out ->
                fun write(c: Csg) {
                    c.polygons.forEach { p ->
                        p.toTriangles().forEach { t ->
                            out.write(t)
                        }
                    }
                }

                val cy = cylinder()
                val s = sphere(center = Vector(0.0, 8.0, 0.0), radius = 2.0)
                val c = cube(radius = Vector(2.0, 3.0, 5.0))
                val c2 = cube(center = Vector(3.0, 4.0, 2.0), radius = Vector(3.0, 4.0, 2.0))
                write(translate(0.0, 0.0, 10.0).applyTo(cy))
                write(translate(0.0, 0.0, 00.0).applyTo(ring(5.0, 6.0, 1.0)))
                write(s)
                write(c)
                write(c2)

                write(translate(10.0, 0.0, 0.0).applyTo(c2.union(c).union(s)))
                write(translate(20.0, 0.0, 0.0).applyTo(c2.intersect(c)))
                write(translate(20.0, 10.0, 0.0).applyTo(c2.intersect(s)))
                write(translate(30.0, 0.0, 0.0).applyTo(c2.subtract(c)))
                write(translate(40.0, 0.0, 0.0).applyTo(c.subtract(c2)))
                write(translate(30.0, 10.0, 0.0).applyTo(c2.subtract(s)))
                write(translate(40.0, 10.0, 0.0).applyTo(s.subtract(c2)))
            }
        }
    }

}