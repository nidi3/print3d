package guru.nidi.print3d.csg

import java.lang.Math.floor
import java.lang.Math.random

class Node private constructor(private val polygons: MutableList<Polygon>, private var plane: Plane?,
                               private var front: Node?, private var back: Node?) {
    constructor() : this(mutableListOf<Polygon>(), null, null, null)
    constructor(polygons: List<Polygon>) : this() {
        build(polygons.toMutableList())
    }

    fun copy(): Node = Node(polygons.toMutableList(), plane?.copy(), front?.copy(), back?.copy())

    fun invert(): Node = Node(polygons.mapTo(mutableListOf()) { it.flip() }, plane?.flip(), back?.invert(), front?.invert())

    private fun clipPolygons(polygons: List<Polygon>): MutableList<Polygon> {
        if (plane == null) return polygons.toMutableList()
        var f = mutableListOf<Polygon>()
        var b = mutableListOf<Polygon>()
        polygons.forEach {
            plane!!.splitPolygon(it, f, b, f, b)
        }
        if (front != null) f = front!!.clipPolygons(f)
        b = back?.clipPolygons(b) ?: mutableListOf()
        f.addAll(b)
        return f
    }

    fun clipTo(bsp: Node): Node = Node(bsp.clipPolygons(polygons), plane, front?.clipTo(bsp), back?.clipTo(bsp))

    fun allPolygons(): List<Polygon> =
            polygons + (front?.allPolygons() ?: listOf()) + (back?.allPolygons() ?: listOf())

    fun combine(node: Node) = combine(node.allPolygons())

    fun combine(polygons: List<Polygon>) = copy().build(polygons)

    private fun build(polygons: List<Polygon>): Node {
        plane = plane ?: polygons[0].plane
        val f = mutableListOf<Polygon>()
        val b = mutableListOf<Polygon>()
        polygons.forEach {
            plane!!.splitPolygon(it, this.polygons, this.polygons, f, b)
        }
        if (f.isNotEmpty()) {
            if (front == null) front = Node()
            front!!.build(f)
        }
        if (b.isNotEmpty()) {
            if (back == null) back = Node()
            back!!.build(b)
        }
        return this
    }
}