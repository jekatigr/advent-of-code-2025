package day02

import utils.runDaySolutions

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseRanges(input: String): List<LongRange> {
        return input.split(",").map { rangeStr ->
            rangeStr.split("-").let {
                LongRange(it[0].toLong(), it[1].toLong())
            }
        }
    }

    fun part1(input: List<String>): Long {
        var result = 0L

        val ranges = parseRanges(input[0])

        for (range in ranges) {
            for (i in range.first..range.last) {
                val s = i.toString()
                val len = s.length

                if (len % 2 != 0) {
                    continue
                }

                if (s.take(len / 2) == s.substring(len / 2)) {
                    result += i
                }
            }
        }

        return result
    }

    fun checkRepeatable(s: String, partLen: Int): Boolean {
        val target = s.take(partLen)

        for (i in partLen..s.length - partLen step partLen) {
            if (s.substring(i, i + partLen) != target) {
                return false
            }
        }

        return true
    }

    fun isInvalid(number: Long): Boolean {
        val s = number.toString()
        val len = s.length

        for (i in 1..len / 2) {
            if (len % i == 0 && checkRepeatable(s, i)) {
                return true
            }
        }

        return false
    }

    fun part2(input: List<String>): Long {
        var result = 0L

        val ranges = parseRanges(input[0])

        for (range in ranges) {
            for (i in range.first..range.last) {
                if (isInvalid(i)) {
                    result += i
                }
            }
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2,  setOf())
}
