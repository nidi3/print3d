package guru.nidi.print3d.csg

import io.kotlintest.specs.StringSpec
import java.io.File

class CsgTest : StringSpec() {
    init {
        "create" {
            model {
                val cy = cylinder()
                val r = ring(center = v(2, 2, 0), radius = 2.0)
                val s = sphere(center = v(0, 8, 0), radius = 2.0)
                val c = cube(radius = v(2, 3, 5))
                val c2 = cube(center = v(3, 4, 2), radius = v(3, 4, 2))
                add(cy.translate(v(0, 0, 10)))
                add(r)
                add(s)
                add(c)
                add(c2)

                add((c2 + c + s).translate(v(10, 0, 0)))

                fun ops(a: Csg, b: Csg) {
                    add((a * b).translate(v(20, 0, 0)))
                    add((a - b).translate(v(30, 0, 0)))
                    add((b - a).translate(v(40, 0, 0)))
                }

                ops(c2, c)

                transform(translate(v(0, 10, 0))) {
                    ops(c2, s)
                }

                transform(translate(v(0, 25, 0))) {
                    ops(c, r)
                }

                write(File("target/csg.stl"), "csg")
            }
        }
    }
}