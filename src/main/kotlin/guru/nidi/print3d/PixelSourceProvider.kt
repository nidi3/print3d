package guru.nidi.print3d

interface PixelSourceProvider {
    fun sourceFor(lat: Int, lng: Int): PixelSource?
}




