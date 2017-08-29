package guru.nidi.print3d.util

import guru.nidi.print3d.csg.Model
import io.kotlintest.specs.StringSpec
import java.io.File
import java.lang.Math.*

class CreateTest : StringSpec() {
    init {
        "load" {
            Model().apply {
                val z = translate(v(-.5, -.5, -.5))
//                    out.write(z.applyTo(cube(10.0, 1.0, 1.0)))
//                    out.write(z.applyTo(cube(1.0, 20.0, 1.0)))
//                    out.write(z.applyTo(cube(1.0, 1.0, 30.0)))
                val f = 8
                val r = 10.0
                val ri = ring(unit, r, .2, .2)
                addToModel(ri)
                for (i in 1 until f) {
                    val a = i * PI / 2 / f
                    val r1 = r * cos(a)
                    addToModel(ring(unit, r1, .2, .2).translate(v(0, 0, r * sin(a))))
                    addToModel(ring(unit, r1, .2, .2).translate(v(0, 0, -r * sin(a))))
                }
//                    out.write(r)

                for (i in 0 until f) {
                    addToModel(ri.rotateZ(i * PI / f).rotateX(PI / 2))
                }

                write(File("target/ring.stl"), "ring")
            }
        }
    }
}