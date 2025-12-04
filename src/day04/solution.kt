package day04

import utils.Matrix
import utils.Points
import utils.runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun getAccessibleRolls(field: Matrix<Char>): Points {
        return field.findAll() { value, i, j ->
            if (value != '@') {
                return@findAll false
            }

            field.getNeighbours(i, j).count { field.get(it) == '@' } < 4
        }
    }

    fun part1(input: List<String>): Int {
        val field = Matrix.from<Char>(input, '.') { it }

        val cells = getAccessibleRolls(field)

        return cells.size
    }

    fun part2(input: List<String>): Long {
        var result = 0L

        val field = Matrix.from<Char>(input, '.') { it }

        var cells: Points

        do {
            cells = getAccessibleRolls(field)
            result += cells.size

            for (cell in cells) {
                field.set('.', cell)
            }
        } while (cells.isNotEmpty())

        return result
    }

    runDaySolutions(day, ::part1, ::part2,  setOf())
}
