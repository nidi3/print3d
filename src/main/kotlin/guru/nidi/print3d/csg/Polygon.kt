package guru.nidi.print3d.csg

data class Polygon(val vertices: List<Vertex>, val props: Map<String, *> = mapOf<String, String>()) {
    constructor(vararg vs: Vertex) : this(vs.asList())
    constructor(props: Map<String, *>, vararg vs: Vertex) : this(vs.asList(), props)

    val plane = Plane.fromPoints(vertices[0].pos, vertices[1].pos, vertices[2].pos)

    fun flip() = Polygon(vertices.reversed().map { it.flip() }, props)

    //TODO support concave
    fun toTriangles(): List<Polygon> {
        val res = mutableListOf<Polygon>()
        for (i in 2 until vertices.size) {
            res.add(Polygon(listOf(vertices[0], vertices[i - 1], vertices[i]), props))
        }
        return res
    }
}