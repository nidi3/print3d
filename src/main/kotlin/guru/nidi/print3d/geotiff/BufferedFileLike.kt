package guru.nidi.print3d.geotiff

import java.io.BufferedInputStream
import java.io.InputStream

class BufferedFileLike(f: InputStream, size: Int) : FileLike {
    private val data = ByteArray(size)
    private var pos = 0

    init {
        BufferedInputStream(f).read(data)
    }

    override fun close() {}

    override fun readShort(): Int {
        pos += 2
        return (((data.int(pos - 2) shl 8) + data.int(pos - 1))) and 0xffff
    }

    override fun readInt(): Int {
        pos += 4
        return (data.int(pos - 4) shl 24) + (data.int(pos - 3) shl 16) + (data.int(pos - 2) shl 8) + data.int(pos - 1)
    }

    override fun readLong(): Long = (readInt().toLong() shl 32) + (readInt().toLong() and 0xFFFFFFFF)

    override fun read(buf: ByteArray) {
        System.arraycopy(data, pos, buf, 0, buf.size)
        pos += buf.size
    }

    override fun getPos() = pos.toLong()

    override fun seek(pos: Long) {
        this.pos = pos.toInt()
    }

}
