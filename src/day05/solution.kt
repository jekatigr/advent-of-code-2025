package day05

import utils.runDaySolutions

class Database(val ranges: MutableList<LongRange>, val ids: MutableList<Long>) {
    fun countFreshIngredients(): Long {
        var count = 0L

        for (id in ids) {
            for(range in ranges) {
                if (id in range) {
                    count += 1

                    break
                }
            }
        }

        return count
    }

    fun countFreshIngredientsInRanges(): Long {
        val rangesMerged = mergeRanges(ranges)

        var count = 0L

        for (range in rangesMerged) {
            count += range.last - range.first + 1
        }

        return count
    }

    private fun mergeRanges(ranges: MutableList<LongRange>): List<LongRange> {
        val rangesMerged = mutableListOf<LongRange>()

        ranges.sortWith(compareBy { it.first })

        for (range in ranges) {
            if (rangesMerged.isEmpty() || rangesMerged.last().last < range.first) {
                rangesMerged += range

                continue
            }

            val last = rangesMerged.removeLast()

            rangesMerged += LongRange(last.first, range.last.coerceAtLeast(last.last))
        }

        return rangesMerged
    }

    companion object {
        fun from(input: List<String>): Database {
            val ranges = mutableListOf<LongRange>()
            val ids = mutableListOf<Long>()

            var isIds = false

            for (line in input) {
                if (line.isEmpty()) {
                    isIds = true
                    continue
                }

                if (isIds) {
                    ids += line.toLong()

                    continue
                }

                ranges.add(
                    LongRange(line.split("-")[0].toLong(),
                        line.split("-")[1].toLong())
                )
            }

            return Database(ranges, ids)
        }
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun part1(input: List<String>): Long {
        val db = Database.from(input)

        return db.countFreshIngredients()
    }

    fun part2(input: List<String>): Long {
        val db = Database.from(input)

        return db.countFreshIngredientsInRanges()
    }

    runDaySolutions(day, ::part1, ::part2,  setOf())
}
