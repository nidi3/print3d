package guru.nidi.print3d.geotiff

interface FileLike {
    fun close()

    fun seek(pos: Long)

    fun getPos(): Long

    fun read(buf: ByteArray)

    fun readShort(): Int

    fun readInt(): Int

    fun readLong(): Long
}

fun ByteArray.int(pos: Int): Int = this[pos].toInt() and 0xff
