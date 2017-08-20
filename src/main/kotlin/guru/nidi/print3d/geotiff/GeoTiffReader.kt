package guru.nidi.print3d.geotiff

import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class GeoTiffReader(input: InputStream, size: Int) {
    companion object {
        fun read(file: File): GeoTiff = read(FileInputStream(file), file.length().toInt())
        fun read(input: InputStream, size: Int): GeoTiff = GeoTiffReader(input, size).load()
    }

    val inp = EndianAwareRandomAccessFile(BufferedFileLike(input, size))

    var width = 0
    var height = 0
    var bitsPerSample = 0
    val bytesPerPixel = 2
    val info = mutableMapOf<String, String>()
    var stripOffsets: IntArray? = null
    var rowsPerStrip = 0
    var stripByteCounts: IntArray? = null
    var xResolution = 0.0
    var yResolution = 0.0
    var resolutionUnit = 2
    var modelPixelScale: DoubleArray = DoubleArray(3) { _ -> 1.toDouble() }
    var modelTiepoints: DoubleArray? = null
    var geoKeyDirectory: IntArray? = null
    var strips: List<ByteArray>? = null

    internal fun load(): GeoTiff {
        val endian = inp.readShort()
        when (endian) {
            0x4949 -> inp.bigEndian = false
            0x4d4d -> inp.bigEndian = true
            else -> throw IllegalArgumentException("Invalid endian $endian")
        }

        val magic = inp.readShort()
        if (magic != 42) throw IllegalArgumentException("Invalid magic number $magic")

        readDirectory()
        inp.close()

        return GeoTiff(this, width, height, bitsPerSample, info,
                xResolution, yResolution, resolutionUnit,
                modelPixelScale, modelTiepoints, geoKeyDirectory)
    }


    private fun readDirectory() {
        val pos = inp.readInt()
        if (pos != 0) {
            inp.seek(pos.toLong())
            val len = inp.readShort()
            for (i in 0 until len) {
                readEntry()
            }
            inp.doAt(0) {
                readStrips()
            }
            readDirectory()
        }
    }

    private fun readEntry() {
        val tag = inp.readShort()
        val typ = inp.readShort()
        val len = inp.readInt()
        val pos = inp.readInt().toLong()

        fun readScalar(): Int {
            if (typ != 1 && typ != 3 && typ != 4) throw UnsupportedOperationException("Unknown type $typ")
            return pos.toInt()
        }

        fun readString(): String {
            if (typ != 2) throw  UnsupportedOperationException("Unknown type $typ")
            return inp.doAt(pos) {
                inp.readAscii(len - 1)
            }
        }

        fun readFrac(): Double {
            if (typ != 5) throw UnsupportedOperationException("Unknown type $typ")
            return inp.doAt(pos) {
                inp.readInt().toDouble() / inp.readInt()
            }
        }

        fun readDoubles(): DoubleArray {
            if (typ != 12) throw UnsupportedOperationException("Unknown type $typ")
            return inp.doAt(pos) {
                val res = DoubleArray(len)
                for (i in 0 until len) res[i] = inp.readDouble()
                res
            }
        }

        fun readScalars(): IntArray {
            if (typ != 1 && typ != 3 && typ != 4) throw UnsupportedOperationException("Unknown type $typ")
            return inp.doAt(pos) {
                val res = IntArray(len)
                for (i in 0 until len) res[i] = when (typ) {
                    3 -> inp.readShort()
                    4 -> inp.readInt()
                    else -> throw UnsupportedOperationException("Unknown type $typ")
                }
                res
            }
        }

        when (tag) {
            256 -> width = readScalar()
            257 -> height = readScalar()
            258 -> bitsPerSample = readScalar()
            259 -> if (readScalar() != 1) throw  UnsupportedOperationException("Unknown compression")
        //262 -> //ignore photometric interpretation
            266 -> if (readScalar() != 1) throw  UnsupportedOperationException("Unknown fill order")
            269 -> info.put("documentName", readString())
            270 -> info.put("imageDescription", readString())
            273 -> stripOffsets = readScalars()
        //274 -> //ignore orientation
            277 -> if (readScalar() != 1) throw  UnsupportedOperationException("Unknown samples per pixel")
            278 -> rowsPerStrip = readScalar()
            279 -> stripByteCounts = readScalars()
            282 -> xResolution = readFrac()
            283 -> yResolution = readFrac()
            284 -> if (readScalar() != 1) throw  UnsupportedOperationException("Unknown planar configuration")
            296 -> resolutionUnit = readScalar()
            305 -> info.put("software", readString())
            306 -> info.put("dateTime", readString())
            339 -> if (readScalar() != 2) throw  UnsupportedOperationException("Unknown sample format")
            -31986 -> modelPixelScale = readDoubles()
            -31614 -> modelTiepoints = readDoubles()
            -30801 -> geoKeyDirectory = readScalars()
        }
    }

    private fun readStrips() {
        val stripCount = height / rowsPerStrip
        strips = (0 until stripCount).map {
            inp.seek(stripOffsets!![it].toLong())
            inp.read(rowsPerStrip * width * bytesPerPixel)
        }
    }

    internal fun getPixel(x: Int, y: Int): Int {
        val s = y / rowsPerStrip
        val pos = (x + (y % rowsPerStrip) * width) * bytesPerPixel
        return inp.readShort(strips!![s], pos)
    }

    internal fun doWithPixels(y: Int, work: (Int, Int) -> Unit) {
        val s = y / rowsPerStrip
        val pos = (y % rowsPerStrip) * width * bytesPerPixel
        var x = 0
        while (x < width) {
            work(x, inp.readShort(strips!![s], pos + x * bytesPerPixel))
            x += 1
        }
    }
}
