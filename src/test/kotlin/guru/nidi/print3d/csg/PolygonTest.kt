package guru.nidi.print3d.csg

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec

class PolygonTest : StringSpec() {
    init {
        "flip" {
            val a = Vector(1.0, 2.0, 3.0)
            val b = Vector(1.0, 2.0, 3.0)
            val c = Vector(1.0, 2.0, 3.0)
            Polygon(Vertex(a, a), Vertex(b, b), Vertex(c, c)).flip() shouldEqual
                    Polygon(Vertex(c, -c), Vertex(b, -b), Vertex(a, -a))
        }
    }
}