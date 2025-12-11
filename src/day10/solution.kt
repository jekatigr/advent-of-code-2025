package day10

import com.microsoft.z3.ArithExpr
import com.microsoft.z3.Context
import utils.runDaySolutions
import kotlin.math.pow


class Machine(val indicator: String, val buttons: List<List<Int>>, val joltages: Array<Int>) {
    val indicatorTarget = indicator.map { ch ->
        if (ch == '.') {
            "0"
        } else {
            "1"
        }
    }.joinToString("").toInt(2)

    fun getMinimumButtonsPressedToEnableMachine(): Int {
        val buttonsBinary = buttons.map {
                lightNums ->
            lightNums.fold(0) { acc, cur -> acc + (2.0.pow(indicator.length - cur - 1)).toInt() }
        }.toSet()

        for (pressesLeft in 1..buttons.size) {
            if (isPressedCountEnough(buttonsBinary, pressesLeft, 0, mutableSetOf())) {
                return pressesLeft
            }
        }

        return -1
    }

    private fun isPressedCountEnough(buttonsBinary: Set<Int>, pressesLeft: Int, current: Int, pressed: MutableSet<Int>): Boolean {
        if (current == indicatorTarget) {
            return true
        }

        if (pressesLeft == 0) {
            return false
        }

        for (button in buttonsBinary) {
            if (pressed.contains(button)) {
                continue
            }

            pressed.add(button)
            if (isPressedCountEnough(buttonsBinary, pressesLeft - 1, current xor button, pressed)) {
                pressed.remove(button)

                return true
            }
            pressed.remove(button)
        }

        return false
    }

    fun getMinimumButtonsPressedToAdjustJoltages2(): Long {
        val buttonsBinary = buttons.map {
                lightNums ->
            lightNums.fold(0) { acc, cur -> acc + (2.0.pow(indicator.length - cur - 1)).toInt() }
        }.toSet()

        val ctx = Context()

        val buttonsBinaryMatrix = buttonsBinary.map { button ->
            button.toString(2).padStart(joltages.size, '0').map { ctx.mkInt(it.toString().toInt()) }
        }

        val variables = buttons.mapIndexed { i, _ -> ctx.mkIntConst("b_${i}") }

        val o = ctx.mkOptimize()

        for (column in joltages.indices) {
            var columnSum: ArithExpr<*> = ctx.mkInt(0)

            for (i in buttonsBinaryMatrix.indices) {
                columnSum = ctx.mkAdd(columnSum, ctx.mkMul(buttonsBinaryMatrix[i][column], variables[i]))
            }

            o.Add(ctx.mkEq(columnSum, ctx.mkInt(joltages[column])))
        }

        for (variable in variables) {
            o.Add(ctx.mkGe(variable,ctx.mkInt(0)))
        }

        val time: ArithExpr<*> = variables.fold(ctx.mkAdd(ctx.mkInt(0))) { acc, cur -> ctx.mkAdd(acc, cur) }
        o.MkMinimize(time)

        val result = o.Check()

        if (result.name != "SATISFIABLE") {
            return -1
        }

        val model = o.getModel()

        val results = variables.map { model.eval(it, false).toString().toInt() }

        return results.fold(0L) { acc, cur -> acc + cur }
    }

    companion object {
        fun from(line: String): Machine {
            val arr = line.split("]")

            val ind = arr[0].removePrefix("[")

            val buttons = arr[1]
                .takeWhile { it != '{' }
                .trim()
                .split(" ")
                .map {
                    it
                        .removePrefix("(")
                        .removeSuffix(")")
                        .split(",")
                        .map { num -> num.toInt() }
                }

            val joltages = arr[1]
                .split("{")[1]
                    .removeSuffix("}")
                    .split(",")
                    .map { it.toInt() }
                .toTypedArray()

            return Machine(ind, buttons, joltages)
        }
    }
}


fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun part1(input: List<String>): Long {
        val machines = input.map { Machine.from(it) }

        return machines.fold(0L) { acc, cur -> acc + cur.getMinimumButtonsPressedToEnableMachine() }
    }

    fun part2(input: List<String>): Long {
        val machines = input.map { Machine.from(it) }

        var result = 0L

        for (i in machines.indices) {
            result += machines[i].getMinimumButtonsPressedToAdjustJoltages2()
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2,  setOf())
}
