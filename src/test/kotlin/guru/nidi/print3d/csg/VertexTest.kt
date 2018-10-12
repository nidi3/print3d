package guru.nidi.print3d.csg

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec

class VertexTest : StringSpec() {
    init {
        "flip" {
            val pos = Vector(1.0, 2.0, 3.0)
            val normal = Vector(4.0, 5.0, 6.0)
            -Vertex(pos, normal) shouldEqual Vertex(pos, -normal)
        }
        "interpolate" {
            val a = Vertex(Vector(1.0, 2.0, 3.0), Vector(4.0, 5.0, 6.0))
            val b = Vertex(Vector(3.0, 5.0, 7.0), Vector(6.0, 4.0, 5.0))
            a.interpolate(b, 0.0) shouldEqual a
            a.interpolate(b, 0.5) shouldEqual Vertex(Vector(2.0, 3.5, 5.0), Vector(5.0, 4.5, 5.5))
            a.interpolate(b, 1.0) shouldEqual b
        }
    }
}