package guru.nidi.print3d

data class LatLng(val lat: Double, val lng: Double) {
    operator fun plus(p: LatLng): LatLng = LatLng(lat + p.lat, lng + p.lng)

    operator fun minus(p: LatLng): LatLng = LatLng(lat - p.lat, lng - p.lng)

    fun normalize() = LatLng(lat, (lng + 180 + 360000) % 360 - 180) //ensure modulo is always taken from positive value
}