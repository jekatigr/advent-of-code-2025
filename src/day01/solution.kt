package day01

import utils.Side
import utils.Skip
import utils.runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun part1(input: List<String>): Long {
        var result = 0L

        var current = 50;

        val moves = getMoves(input)

        for (move in moves) {
            if (move.first == Side.LEFT) {
                current -= move.second
            } else {
                current += move.second
            }

            while (current !in 0..<100) {

                if (current >= 100) {
                    current -= 100
                } else {
                    current += 100
                }
            }

            if (current == 0) {
                result += 1
            }
        }

        return result
    }

    fun part2(input: List<String>): Long {
        var result = 0L

        var current = 50;

        val moves = getMoves(input)

        for (move in moves) {
            val n = move.second

            for (i in 1..n) {
                if (move.first == Side.LEFT) {
                    current -= 1
                } else {
                    current += 1
                }

                if (current >= 100) {
                    current -= 100;
                }

                if (current < 0) {
                     current += 100;
                }

                if (current == 0) {
                    result += 1
                }
            }
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2,  setOf())
}

fun getMoves(input: List<String>): MutableList<Pair<Side, Int>> {
    val result = mutableListOf<Pair<Side, Int>>()

    for (line in input) {
        val n = line.substring(1).toInt()

        if (line[0] == 'L') {
            result.add(Pair(Side.LEFT, n))
        } else {
            result.add(Pair(Side.RIGHT, n))
        }
    }

    return result
}
