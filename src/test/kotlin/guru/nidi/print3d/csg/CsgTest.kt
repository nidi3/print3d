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
                val r = ring(center = Vector(2.0, 2.0, 0.0), radius = 2.0)
                val s = sphere(center = Vector(0.0, 8.0, 0.0), radius = 2.0)
                val c = cube(radius = Vector(2.0, 3.0, 5.0))
                val c2 = cube(center = Vector(3.0, 4.0, 2.0), radius = Vector(3.0, 4.0, 2.0))
                write(translate(0.0, 0.0, 10.0).applyTo(cy))
                write(r)
                write(s)
                write(c)
                write(c2)

                write(translate(10.0, 0.0, 0.0).applyTo(c2.union(c).union(s)))
                write(translate(20.0, 0.0, 0.0).applyTo(c2.intersect(c)))
                write(translate(20.0, 10.0, 0.0).applyTo(c2.intersect(s)))
                write(translate(20.0, 25.0, 0.0).applyTo(c.intersect(r)))
                write(translate(30.0, 0.0, 0.0).applyTo(c2.subtract(c)))
                write(translate(40.0, 0.0, 0.0).applyTo(c.subtract(c2)))
                write(translate(30.0, 10.0, 0.0).applyTo(c2.subtract(s)))
                write(translate(40.0, 10.0, 0.0).applyTo(s.subtract(c2)))
                write(translate(30.0, 25.0, 0.0).applyTo(c.subtract(r)))
                write(translate(40.0, 25.0, 0.0).applyTo(r.subtract(c)))
            }
        }
    }

}