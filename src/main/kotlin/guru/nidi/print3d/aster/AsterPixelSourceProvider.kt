package guru.nidi.print3d.aster

import guru.nidi.print3d.PixelSource
import guru.nidi.print3d.PixelSourceProvider

class AsterPixelSourceProvider(val aster: AsterFile) : PixelSourceProvider {
    override fun sourceFor(lat: Int, lng: Int): PixelSource? = aster.getTile(lat, lng)
}
