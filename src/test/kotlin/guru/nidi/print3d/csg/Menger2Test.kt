package guru.nidi.print3d.csg

import io.kotlintest.specs.StringSpec
import java.io.File

class Menger2Test : StringSpec() {
    init {
        "create" {
            model {
                val mmm = cube(center = v(-2, -2, -2))
                val mmo = cube(center = v(-2, -2, 0))
                val mmp = cube(center = v(-2, -2, 2))
                val mom = cube(center = v(-2, 0, -2))
                val moo = cube(center = v(-2, 0, 0))
                val mop = cube(center = v(-2, 0, 2))
                val mpm = cube(center = v(-2, 2, -2))
                val mpo = cube(center = v(-2, 2, 0))
                val mpp = cube(center = v(-2, 2, 2))
                val omm = cube(center = v(0, -2, -2))
                val omo = cube(center = v(0, -2, 0))
                val omp = cube(center = v(0, -2, 2))
                val oom = cube(center = v(0, 0, -2))
                val ooo = cube(center = v(0, 0, 0))
                val oop = cube(center = v(0, 0, 2))
                val opm = cube(center = v(0, 2, -2))
                val opo = cube(center = v(0, 2, 0))
                val opp = cube(center = v(0, 2, 2))
                val pmm = cube(center = v(2, -2, -2))
                val pmo = cube(center = v(2, -2, 0))
                val pmp = cube(center = v(2, -2, 2))
                val pom = cube(center = v(2, 0, -2))
                val poo = cube(center = v(2, 0, 0))
                val pop = cube(center = v(2, 0, 2))
                val ppm = cube(center = v(2, 2, -2))
                val ppo = cube(center = v(2, 2, 0))
                val ppp = cube(center = v(2, 2, 2))

                fun mosely(level: Int) {
                    if (level == 0) {
                        add(ppo)
                        add(mmo)
                        add(pmo)
                        add(mpo)

                        add(pop)
                        add(mom)
                        add(mop)
                        add(pom)

                        add(opp)
                        add(omm)
                        add(omp)
                        add(opm)

                        add(poo)
                        add(opo)
                        add(oop)
                        add(moo)
                        add(omo)
                        add(oom)
                    } else {
                        transform(scale(unit / 3.0)) {
                            transform(translate(v(6, 6, 0))) { mosely(level - 1) }
                            transform(translate(v(-6, -6, 0))) { mosely(level - 1) }
                            transform(translate(v(6, -6, 0))) { mosely(level - 1) }
                            transform(translate(v(-6, 6, 0))) { mosely(level - 1) }

                            transform(translate(v(6, 0, 6))) { mosely(level - 1) }
                            transform(translate(v(-6, 0, -6))) { mosely(level - 1) }
                            transform(translate(v(-6, 0, 6))) { mosely(level - 1) }
                            transform(translate(v(6, 0, -6))) { mosely(level - 1) }

                            transform(translate(v(0, 6, 6))) { mosely(level - 1) }
                            transform(translate(v(0, -6, -6))) { mosely(level - 1) }
                            transform(translate(v(0, -6, 6))) { mosely(level - 1) }
                            transform(translate(v(0, 6, -6))) { mosely(level - 1) }

                            transform(translate(v(6, 0, 0))) { mosely(level - 1) }
                            transform(translate(v(0, 6, 0))) { mosely(level - 1) }
                            transform(translate(v(0, 0, 6))) { mosely(level - 1) }

                            transform(translate(v(-6, 0, 0))) { mosely(level - 1) }
                            transform(translate(v(0, -6, 0))) { mosely(level - 1) }
                            transform(translate(v(0, 0, -6))) { mosely(level - 1) }
                        }
                    }
                }

                fun menger(level: Int) {
                    if (level == 0) {
                        add(ppo)
                        add(mmo)
                        add(pmo)
                        add(mpo)

                        add(pop)
                        add(mom)
                        add(mop)
                        add(pom)

                        add(opp)
                        add(omm)
                        add(omp)
                        add(opm)

                        add(ppp)
                        add(mmm)

                        add(mpp)
                        add(pmp)
                        add(ppm)
                        add(pmm)
                        add(mpm)
                        add(mmp)
                    } else {
                        transform(scale(unit / 3.0)) {
                            transform(translate(v(6, 6, 0))) { menger(level - 1) }
                            transform(translate(v(6, -6, 0))) { menger(level - 1) }
                            transform(translate(v(-6, 6, 0))) { menger(level - 1) }
                            transform(translate(v(-6, -6, 0))) { menger(level - 1) }

                            transform(translate(v(6, 0, 6))) { menger(level - 1) }
                            transform(translate(v(6, 0, -6))) { menger(level - 1) }
                            transform(translate(v(-6, 0, 6))) { menger(level - 1) }
                            transform(translate(v(-6, 0, -6))) { menger(level - 1) }

                            transform(translate(v(0, 6, 6))) { menger(level - 1) }
                            transform(translate(v(0, 6, -6))) { menger(level - 1) }
                            transform(translate(v(0, -6, 6))) { menger(level - 1) }
                            transform(translate(v(0, -6, -6))) { menger(level - 1) }

                            transform(translate(v(6, 6, 6))) { menger(level - 1) }
                            transform(translate(v(-6, -6, -6))) { menger(level - 1) }

                            transform(translate(v(-6, 6, 6))) { menger(level - 1) }
                            transform(translate(v(6, -6, 6))) { menger(level - 1) }
                            transform(translate(v(6, 6, -6))) { menger(level - 1) }

                            transform(translate(v(6, -6, -6))) { menger(level - 1) }
                            transform(translate(v(-6, 6, -6))) { menger(level - 1) }
                            transform(translate(v(-6, -6, 6))) { menger(level - 1) }
                        }
                    }
                }

                val size = 10
                val level = 1
                transform(scale(unit * size.toDouble())) {
                    menger(level)
                    write(File("target/menger-$level-$size.stl"), "menger")

//                    mosely(level)
//                    write(File("target/mosely-$level-$size.stl"), "mosely")
                }

            }
        }
    }
}