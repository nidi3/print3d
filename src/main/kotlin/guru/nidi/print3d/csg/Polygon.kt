package guru.nidi.print3d.csg

data class Polygon(val vertices: List<Vertex>, val props: Map<String, *> = mapOf<String, String>()) {
    constructor(vararg vs: Vertex) : this(vs.asList())
    constructor(props: Map<String, *>, vararg vs: Vertex) : this(vs.asList(), props)

    val plane = Plane.fromPoints(vertices[0].pos, vertices[1].pos, vertices[2].pos)

    operator fun unaryMinus() = Polygon(vertices.reversed().map { -it }, props)

    fun boundingBox(): Pair<Vector, Vector> {
        var minX = Double.MAX_VALUE
        var maxX = Double.MIN_VALUE
        var minY = Double.MAX_VALUE
        var maxY = Double.MIN_VALUE
        var minZ = Double.MAX_VALUE
        var maxZ = Double.MIN_VALUE
        for (v in vertices) {
            if (v.pos.x < minX) minX = v.pos.x
            if (v.pos.x > maxX) maxX = v.pos.x
            if (v.pos.y < minY) minY = v.pos.y
            if (v.pos.y > maxY) maxY = v.pos.y
            if (v.pos.z < minZ) minZ = v.pos.z
            if (v.pos.z > maxZ) maxZ = v.pos.z
        }
        return Pair(Vector(minX, minY, minZ), Vector(maxX, maxY, maxZ))
    }

    fun size() = boundingBox().let { (it.second - it.first).abs() }

    //TODO support concave
    fun toTriangles(): List<Polygon> {
        val res = mutableListOf<Polygon>()
        for (i in 2 until vertices.size) {
            res.add(Polygon(listOf(vertices[0], vertices[i - 1], vertices[i]), props))
        }
        return res
    }
}