package guru.nidi.print3d.csg

import io.kotlintest.specs.StringSpec
import java.io.File

class MosleyTest : StringSpec() {
    init {
        "create" {
            model {

                fun menger(r: Double, level: Int): Csg {
                    var c = cube(center = origin, radius = unit * r)

                    fun step(r: Double, level: Int) {
                        val e = r / 3
                        val d = 2 * r / 3
                        c -= cube(center = v(0, 0, 0), radius = unit * e)
                        c -= cube(center = v(d, d, d), radius = unit * e)
                        c -= cube(center = v(-d, d, d), radius = unit * e)
                        c -= cube(center = v(d, -d, d), radius = unit * e)
                        c -= cube(center = v(d, d, -d), radius = unit * e)
                        c -= cube(center = v(-d, -d, d), radius = unit * e)
                        c -= cube(center = v(d, -d, -d), radius = unit * e)
                        c -= cube(center = v(-d, d, -d), radius = unit * e)
                        c -= cube(center = v(-d, -d, -d), radius = unit * e)
//                        addToModel(cube(center = origin, radius = v(r / 3, r / 3, 1.1 * r)))
//                        addToModel(cube(center = origin, radius = v(r / 3, 1.1 * r, r / 3)))
//                        addToModel(cube(center = origin, radius = v(1.1 * r, r / 3, r / 3)))
                        if (level > 0) {
                            transform(scale(unit / 3.0)) {
                                transform(translate(v(d, 0, 0))) { step(r, level - 1) }
                                transform(translate(v(-d, 0, 0))) { step(r, level - 1) }
                                transform(translate(v(0, d, 0))) { step(r, level - 1) }
                                transform(translate(v(0, -d, 0))) { step(r, level - 1) }
                                transform(translate(v(0, 0, d))) { step(r, level - 1) }
                                transform(translate(v(0, 0, -d))) { step(r, level - 1) }
                                println("1 $level")

                                transform(translate(v(0, d, d))) { step(r, level - 1) }
                                transform(translate(v(0, -d, d))) { step(r, level - 1) }
                                transform(translate(v(0, d, -d))) { step(r, level - 1) }
                                transform(translate(v(0, -d, -d))) { step(r, level - 1) }
                                println("2 $level")

                                transform(translate(v(d, 0, d))) { step(r, level - 1) }
                                transform(translate(v(-d, 0, d))) { step(r, level - 1) }
                                transform(translate(v(d, 0, -d))) { step(r, level - 1) }
                                transform(translate(v(-d, 0, -d))) { step(r, level - 1) }
                                println("3 $level")

                                transform(translate(v(d, d, 0))) { step(r, level - 1) }
                                transform(translate(v(-d, d, 0))) { step(r, level - 1) }
                                transform(translate(v(d, -d, 0))) { step(r, level - 1) }
                                transform(translate(v(-d, -d, 0))) { step(r, level - 1) }
                                println("4 $level")

                            }
                        }
                    }

                    step(r, level)
                    add(c)
                    return c
                }

                menger(15.0, 2)

                write(File("target/mosley.stl"), "mosley")
            }
        }
    }
}