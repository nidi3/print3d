package guru.nidi.print3d.csg

import java.lang.Math.sqrt

data class Vector(val x: Double, val y: Double, val z: Double) {
    companion object {
        fun ofSpherical(r: Double, theta: Double, phi: Double) =
                Vector(r * Math.sin(theta) * Math.cos(phi), r * Math.sin(theta) * Math.sin(phi), r * Math.cos(theta))
    }

    operator fun unaryMinus() = Vector(-x, -y, -z)
    operator fun plus(a: Vector) = Vector(x + a.x, y + a.y, z + a.z)
    operator fun minus(a: Vector) = Vector(x - a.x, y - a.y, z - a.z)
    operator fun times(a: Double) = Vector(x * a, y * a, z * a)
    operator fun div(a: Double) = Vector(x / a, y / a, z / a)
    infix fun dot(a: Vector) = x * a.x + y * a.y + z * a.z
    infix fun cross(a: Vector) = Vector(y * a.z - z * a.y, z * a.x - x * a.z, x * a.y - y * a.x)
    fun length() = sqrt(this dot this)
    fun unit() = this / length()
    fun interpolate(a: Vector, t: Double) = this + (a - this) * t
}

val origin = Vector(0.0, 0.0, 0.0)