package guru.nidi.print3d.csg

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec

class NodeTest : StringSpec() {
    init {
        "convex node" {
            val n = Vector(0.0, 0.0, 1.0)
            val p1 = Polygon(Vertex(Vector(0.0, 0.0, 0.0), n), Vertex(Vector(1.0, 0.0, 0.0), n), Vertex(Vector(0.0, 1.0, 0.0), n))
            val p2 = Polygon(Vertex(Vector(0.0, 0.0, 1.0), n), Vertex(Vector(1.0, 0.0, 0.0), n), Vertex(Vector(0.0, 0.0, 0.0), n))
            val p3 = Polygon(Vertex(Vector(0.0, 0.0, 1.0), n), Vertex(Vector(0.0, 1.0, 0.0), n), Vertex(Vector(0.0, 0.0, 0.0), n))
            val no = Node(listOf(p1, p2, p3))
            no.allPolygons() shouldEqual listOf(p1, p2, p3)
        }
    }
}