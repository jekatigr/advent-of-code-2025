package day06

import utils.Matrix
import utils.runDaySolutions

class Task(val op: Char, val operands: Array<Long>) {
    fun getAnswer(): Long {
        if (op == '+') {
            return operands.fold(0L) { acc, value -> acc + value }
        }

        return operands.fold(1L) { acc, value -> acc * value }
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseTasks(input: List<String>): List<Task> {
        val ops = input[input.size - 1].split(" ").filter { it != "" && it != " " }.map { it[0] }

        val operands: MutableList<Array<Long>> = mutableListOf()

        val tasks = mutableListOf<Task>()

        for (i in input.indices) {
            if (i == input.lastIndex) {
                continue
            }

            operands.add(input[i].trim().split(" ").filter { it != "" && it != " " }.map { it.toLong() }.toTypedArray())
        }

        for (i in ops.indices) {
            val operandsForTask = Array(operands.size) { 0L }

            for (j in operands.indices) {
                operandsForTask[j] = operands[j][i]
            }

            tasks.add(Task(ops[i], operandsForTask))
        }

        return tasks
    }

    fun part1(input: List<String>): Long {
        val tasks = parseTasks(input)

        return tasks.fold(0L) { acc, task -> acc + task.getAnswer() }
    }

    fun getColumnTask(field: Matrix<Char>, opI: Int, opJ: Int, width: Int): Task {
        val op = field.get(opI, opJ)

        val operands = mutableListOf<Long>()

        for (j in opJ..<opJ + width) {
            var str = ""
            for (i in 0..field.height() - 2) {
                str += field.get(i, j)
            }

            operands += str.trim().toLong()
        }

        return Task(op, operands.toTypedArray())
    }

    fun parseTasks2(input: List<String>): List<Task> {
        val field = Matrix.from(input, ' ') { it }

        val tasks = mutableListOf<Task>()

        var width = 0
        val opRow = field.height() - 1
        var j = 0
        var opJ = 0
        while (j in field.widthRange()) {
            if (field.get(opRow, j) != ' ') { // op
                opJ = j
                j += 1
                width = 1
            }

            while (field.isValidCoordinates(opRow, j) && field.get(opRow, j) == ' ') {
                width += 1
                j += 1
            }

            if (j < field.width()) {
                width -= 1
            }

            tasks.add(getColumnTask(field, opRow, opJ, width))
        }

        return tasks
    }

    fun part2(input: List<String>): Long {
        val maxLen = input.maxOfOrNull { it.length }!!
        val tasks = parseTasks2(input.map { it.padEnd(maxLen, ' ') })

        return tasks.fold(0L) { acc, task -> acc + task.getAnswer() }
    }

    runDaySolutions(day, ::part1, ::part2,  setOf())
}
