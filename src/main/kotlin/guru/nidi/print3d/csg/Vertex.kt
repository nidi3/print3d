package guru.nidi.print3d.csg

data class Vertex(val pos: Vector, val normal: Vector) {
    operator fun unaryMinus() = Vertex(pos, -normal)
    fun interpolate(other: Vertex, t: Double) = Vertex(pos.interpolate(other.pos, t), normal.interpolate(other.normal, t))
}