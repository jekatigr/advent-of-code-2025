package day03

import utils.Skip
import utils.runDaySolutions
import kotlin.math.max

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseBanks(input: List<String>): Array<Array<Int>> {
        return input.map { line -> line.toCharArray().map { it.digitToInt() }.toTypedArray() }.toTypedArray()
    }

    fun getMaxJoltageFor2(bank: Array<Int>): Int {
        var max = 0

        for (i in bank.indices) {
            for (j in i+1..<bank.size) {
                val value = bank[i] * 10 + bank[j]
                if (value > max) {
                    max = value
                }
            }
        }

        return max
    }

    fun getMaxJoltageFor12(bank: Array<Int>): Long {
        var dp = Array(13) { Array(bank.size) { 0L } }

        // init
        for (i in bank.indices) {
            dp[0][i] = 0L
        }

        for (taken in 1..12) {
            for (i in bank.indices) {
                if (i == 0) {
                    dp[taken][i] = bank[i].toLong()

                    continue
                }

                dp[taken][i] = max(
                    dp[taken][i - 1],
                    dp[taken - 1][i - 1] * 10 + bank[i]
                )
            }
        }

        return dp[12][bank.size - 1];
    }

    fun part1(input: List<String>): Long {
        var result = 0L

        val banks = parseBanks(input);

        for (bank in banks) {
            result += getMaxJoltageFor2(bank);
        }

        return result
    }

    fun part2(input: List<String>): Long {
        var result = 0L

        val banks = parseBanks(input);

        for (bank in banks) {
            result += getMaxJoltageFor12(bank);
        }

        return result
    }

    runDaySolutions(day, ::part1, ::part2,  setOf())
}


