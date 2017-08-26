package guru.nidi.print3d.csg

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec
import java.lang.Math.sqrt

class VectorTest : StringSpec() {
    init {
        "neg" {
            -Vector(1.0, 2.0, 3.0) shouldEqual Vector(-1.0, -2.0, -3.0)
        }
        "plus" {
            Vector(1.0, 2.0, 3.0) + Vector(5.0, 6.0, 7.0) shouldEqual Vector(6.0, 8.0, 10.0)
        }
        "minus" {
            Vector(1.0, 2.0, 3.0) - Vector(5.0, 4.0, 3.0) shouldEqual Vector(-4.0, -2.0, 0.0)
        }
        "mult" {
            Vector(1.0, 2.0, 3.0) * 2.0 shouldEqual Vector(2.0, 4.0, 6.0)
        }
        "div" {
            Vector(1.0, 2.0, 3.0) / 2.0 shouldEqual Vector(.5, 1.0, 1.5)
        }
        "dot"{
            Vector(1.0, 2.0, 3.0) dot Vector(2.0, 3.0, 4.0) shouldEqual 20.0
        }
        "cross"{
            Vector(1.0, 2.0, 3.0) cross Vector(2.0, 3.0, 4.0) shouldEqual Vector(-1.0, 2.0, -1.0)
        }
        "length"{
            Vector(2.0, 3.0, 4.0).length() shouldEqual sqrt(4.0 + 9.0 + 16.0)
        }
        "unit"{
            val len = sqrt(4.0 + 16.0 + 64.0)
            Vector(2.0, 4.0, 8.0).unit() shouldEqual Vector(2.0 / len, 4.0 / len, 8.0 / len)
        }
        "interpolate"{
            val v = Vector(3.0, 5.0, 7.0)
            Vector(2.0, 3.0, 4.0).interpolate(v, 0.0) shouldEqual Vector(2.0, 3.0, 4.0)
            Vector(2.0, 3.0, 4.0).interpolate(v, 0.5) shouldEqual Vector(2.5, 4.0, 5.5)
            Vector(2.0, 3.0, 4.0).interpolate(v, 1.0) shouldEqual Vector(3.0, 5.0, 7.0)
        }
    }
}