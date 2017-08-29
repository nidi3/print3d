package guru.nidi.print3d.csg

import io.kotlintest.specs.StringSpec
import java.io.File

class MengerTest : StringSpec() {
    init {
        "create" {
            Model().apply {

                fun menger(r: Double, level: Int): Csg {
                    var c = cube(center = unit * 30.0)
//                    var c = cube(center = origin, radius = unit * r)

                    fun step(r: Double, level: Int) {
                        c += cube(center = origin, radius = v(r / 3, r / 3, 1.1 * r))
                        c += cube(center = origin, radius = v(r / 3, 1.1 * r, r / 3))
                        c += cube(center = origin, radius = v(1.1 * r, r / 3, r / 3))
//                        addToModel(cube(center = origin, radius = v(r / 3, r / 3, 1.1 * r)))
//                        addToModel(cube(center = origin, radius = v(r / 3, 1.1 * r, r / 3)))
//                        addToModel(cube(center = origin, radius = v(1.1 * r, r / 3, r / 3)))
                        if (level > 0) {
                            transform(scale(unit / 3.0)) {
                                val d = 2 * r / 3
                                transform(translate(v(d, d, 0))) { step(r, level - 1) }
                                transform(translate(v(-d, -d, 0))) { step(r, level - 1) }
                                transform(translate(v(d, -d, 0))) { step(r, level - 1) }
                                transform(translate(v(-d, d, 0))) { step(r, level - 1) }

                                transform(translate(v(d, 0, d))) { step(r, level - 1) }
                                transform(translate(v(-d, 0, -d))) { step(r, level - 1) }
                                transform(translate(v(-d, 0, d))) { step(r, level - 1) }
                                transform(translate(v(d, 0, -d))) { step(r, level - 1) }

                                transform(translate(v(0, d, d))) { step(r, level - 1) }
                                transform(translate(v(0, -d, -d))) { step(r, level - 1) }
                                transform(translate(v(0, -d, d))) { step(r, level - 1) }
                                transform(translate(v(0, d, -d))) { step(r, level - 1) }

                                transform(translate(v(d, d, d))) { step(r, level - 1) }
                                transform(translate(v(-d, -d, -d))) { step(r, level - 1) }

                                transform(translate(v(-d, d, d))) { step(r, level - 1) }
                                transform(translate(v(d, -d, d))) { step(r, level - 1) }
                                transform(translate(v(d, d, -d))) { step(r, level - 1) }

                                transform(translate(v(-d, -d, d))) { step(r, level - 1) }
                                transform(translate(v(-d, d, -d))) { step(r, level - 1) }
                                transform(translate(v(d, -d, -d))) { step(r, level - 1) }
                            }
                        }
                    }

                    step(r, level)
                    addToModel(c)
                    return c
                }

                menger(15.0, 3)

                write(File("target/menger.stl"), "menger")
            }
        }
    }
}