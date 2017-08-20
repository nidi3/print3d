package guru.nidi.print3d.geotiff

import java.nio.charset.StandardCharsets

class EndianAwareRandomAccessFile(val file: FileLike) {
    var bigEndian = true
    fun close() = file.close()

    fun seek(pos: Long) = file.seek(pos)

    fun getPos() = file.getPos()

    fun <T> doAt(pos: Long, action: () -> T): T {
        val oldPos = getPos()
        seek(pos)
        val res = action()
        seek(oldPos)
        return res
    }

    fun read(len: Int): ByteArray {
        val v = ByteArray(len)
        file.read(v)
        return v
    }

    fun readShort(): Int {
        val v = file.readShort()
        return if (bigEndian) v
        else ((v shr 8) and 0xff) + ((v shl 8) and 0xff00)
    }

    fun readShort(data: ByteArray, pos: Int): Int {
        val v = ((data.int(pos) shl 8) + data.int(pos + 1))
        return if (bigEndian) v
        else ((v shr 8) and 0xff) + ((v shl 8) and 0xff00)
    }

    fun readInt(): Int {
        val v = file.readInt()
        return if (bigEndian) v
        else ((v shr 24) and 0xff) + ((v shr 8) and 0xff00) + ((v shl 8) and 0xff0000) + (v shl 24)
    }

    fun readLong(): Long {
        val v = file.readLong()
        return if (bigEndian) v
        else ((v shr 56) and 0xff) + ((v shr 40) and 0xff00) + ((v shr 24) and 0xff0000) + ((v shr 8) and 0xff000000) +
                ((v shl 8) and 0xff00000000L) + ((v shl 24) and 0xff0000000000L) + ((v shl 40) and 0xff000000000000L) + (v shl 56)
    }

    fun readDouble(): Double {
        val v = readLong()
        return java.lang.Double.longBitsToDouble(v)
    }

    fun readAscii(len: Int): String {
        val buf = ByteArray(len)
        file.read(buf)
        return String(buf, StandardCharsets.US_ASCII)
    }
}
