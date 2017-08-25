package guru.nidi.print3d.util

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Triangle

fun triangulate(coords: List<Coordinate>): List<Triangle> {
    val CONCAVE = -1
    val CONVEX = 1

    val cs = coords.toMutableList()
    val tris = mutableListOf<Triangle>()

    fun pred(i: Int) = if (i == 0) cs.lastIndex else i - 1
    fun succ(i: Int) = if (i == cs.lastIndex) 0 else i + 1

    fun spannedAreaSign(p1: Coordinate, p2: Coordinate, p3: Coordinate) =
            Math.signum(p1.x * (p3.y - p2.y) + p2.x * (p1.y - p3.y) + p3.x * (p2.y - p1.y)).toInt()

    fun classify(n: Int) = spannedAreaSign(cs[pred(n)], cs[n], cs[succ(n)])

    fun triangle(n: Int) = Triangle(cs[pred(n)], cs[n], cs[succ(n)])

    val vertexType = cs.mapIndexedTo(mutableListOf()) { i, _ -> classify(i) }

    fun isEarTip(n: Int): Boolean {
        if (vertexType[n] == CONCAVE) return false
        val prev = pred(n)
        val next = succ(n)
        var i = succ(next)
        while (i != prev) {
            if (vertexType[i] != CONVEX) {
                if (spannedAreaSign(cs[next], cs[prev], cs[i]) >= 0) {
                    if (spannedAreaSign(cs[prev], cs[n], cs[i]) >= 0) {
                        if (spannedAreaSign(cs[n], cs[next], cs[i]) >= 0) return false
                    }
                }
            }
            i = succ(i)
        }
        return true
    }

    fun findEarTip(): Int {
        for (i in 0 until cs.size) if (isEarTip(i)) return i
        for (i in 0 until cs.size) if (vertexType[i] != CONCAVE) return i
        return 0
    }

    fun cutEarTip(n: Int) {
        tris.add(triangle(n))
        cs.removeAt(n)
        vertexType.removeAt(n)
    }

    while (cs.size > 3) {
        val n = findEarTip()
        cutEarTip(n)

        val prev = pred(n)
        val next = if (n == cs.size) 0 else n
        vertexType[prev] = classify(prev)
        vertexType[next] = classify(next)
    }

    if (cs.size == 3) {
        tris.add(triangle(1))
    }

    return tris
}
