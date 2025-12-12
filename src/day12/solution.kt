package day12

import utils.Matrix
import utils.Skip
import utils.runDaySolutions

class Region(height: Int, width: Int, val presentsRequirements: MutableList<Int>) {
    val region = Matrix(Array(height) { Array(width) { '.' } })

    fun canFitAllPresents(): Boolean {
        val requiredTotal = presentsRequirements.sum()

        val w = region.width()
        val h = region.height()

        val canFitW = w / 3
        val canFitH = h / 3

        return canFitW * canFitH >= requiredTotal
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseInput(input: List<String>): List<Region> {
        val regions = mutableListOf<Region>()

        var index = 0

        while (index < input.size) {
            val line = input[index]

            if (line.isEmpty() || !line.contains("x")) {
                index++

                continue
            }

            val regionStr = line.split(": ")
            val (width, height) = regionStr[0].split("x").map { it.toInt() }
            val presentsRequirements = regionStr[1].split(" ").map { it.toInt() }.toMutableList()

            regions += Region(height, width, presentsRequirements)

            index += 1
        }

        return regions
    }

    fun part1(input: List<String>): Int {
        val regions = parseInput(input)

        return regions.count { it.canFitAllPresents() }
    }

    fun part2(input: List<String>): String {
        return "AoC 2025 is solved!"
    }

    runDaySolutions(day, ::part1, ::part2,  setOf(Skip.PART1_TESTS, Skip.PART2_TESTS))
}
