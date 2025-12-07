package day07

import utils.Matrix
import utils.runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun part1(input: List<String>): Int {
        val manifold = Matrix.from(input, '.') { it }

        var splitCount = 0

        for (i in manifold.heightRange()) {
            if (i == 0) {
                continue
            }

            for (j in manifold.widthRange()) {
                val top = manifold.get(i - 1, j)
                val current = manifold.get(i, j)

                if (top == 'S') {
                    manifold.set('|', i, j)

                    continue
                }

                if (top == '|' && current == '.') {
                    manifold.set('|', i, j)

                    continue
                }
                if (top == '|' && current == '^') {
                    splitCount += 1

                    if (j > 0 && manifold.get(i, j - 1) != '^') {
                        manifold.set('|', i, j - 1)
                    }

                    if (j < manifold.width() - 1 && manifold.get(i, j + 1) != '^') {
                        manifold.set('|', i, j + 1)
                    }

                    continue
                }
            }
        }

        return splitCount
    }

    class Cell(val char: Char, var ways: Long = 0L)

    fun part2(input: List<String>): Long {
        val quantumManifold = Matrix.from(input, Cell('.')) { Cell(it) }

        for (i in quantumManifold.heightRange()) {
            if (i == 0) {
                continue
            }

            for (j in quantumManifold.widthRange()) {
                val top = quantumManifold.get(i - 1, j)
                val current = quantumManifold.get(i, j)

                if (top.char == 'S') {
                    current.ways = 1

                    continue
                }

                if (current.char == '^') {
                    if (j > 0) {
                        quantumManifold.get(i, j - 1).ways += top.ways
                    }

                    if (j < quantumManifold.width() - 1) {
                        quantumManifold.get(i, j + 1).ways += top.ways
                    }

                    continue
                }

                current.ways += top.ways
            }
        }

        var result = 0L
        val i = quantumManifold.height() - 1
        for (j in quantumManifold.widthRange()) {
            result += quantumManifold.get(i, j).ways
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2,  setOf())
}
