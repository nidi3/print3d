package guru.nidi.print3d.util

import guru.nidi.print3d.csg.*
import guru.nidi.print3d.data.StlBinaryWriter
import io.kotlintest.specs.StringSpec
import java.io.File
import java.lang.Math.*

class CreateTest : StringSpec() {
    init {
        "load" {
            StlBinaryWriter(File("target/ring.stl"), "ring").use { out ->
                val z = translate(-.5, -.5, -.5)
//                    out.write(z.applyTo(cube(10.0, 1.0, 1.0)))
//                    out.write(z.applyTo(cube(1.0, 20.0, 1.0)))
//                    out.write(z.applyTo(cube(1.0, 1.0, 30.0)))
                val f = 8
                val r = 10.0
                val ri = ring(origin, r, .2, .2)
                out.write(ri)
                for (i in 1 until f) {
                    val a = i * PI / 2 / f
                    val r1 = r * cos(a)
                    out.write(translate(0.0, 0.0, r * sin(a)).applyTo(ring(origin, r1, .2, .2)))
                    out.write(translate(0.0, 0.0, -r * sin(a)).applyTo(ring(origin, r1, .2, .2)))
                }
//                    out.write(r)

                for (i in 0 until f) {
                    out.write(rotateZ(i * PI / f).rotateX(PI / 2).applyTo(ri))
                }
            }
        }
    }
}