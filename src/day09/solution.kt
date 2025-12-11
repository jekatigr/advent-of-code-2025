package day09

import utils.Matrix
import utils.runDaySolutions
import utils.Point
import java.util.PriorityQueue
import kotlin.math.abs

open class FloorPoint(var first: Int, var second: Int)

class EdgePoint(first: Int, second: Int): FloorPoint(first, second) {
    companion object {
        fun from(p: Point): EdgePoint = EdgePoint(p.first, p.second)
    }

    override fun toString(): String {
        return "X"
    }
}

class CompressedPoint(first: Int, second: Int, val original: Point, val originalOrder: Int): FloorPoint(first, second) {
    companion object {
        fun from(p: Point, index: Int): CompressedPoint = CompressedPoint(p.first, p.second, p, index)
    }

    override fun toString(): String {
        return "#"
    }
}

class OutsidePoint(p: Point): FloorPoint(p.first, p.second) {
    override fun toString(): String {
        return "."
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun getSquare(p1: Point, p2: Point): Long {
        return (abs(p1.first - p2.first) + 1).toLong() * (abs(p1.second - p2.second) + 1).toLong()
    }

    fun part1(input: List<String>): Long {
        val points = input.map { Point(it.split(",")[0].toInt(), it.split(",")[1].toInt()) }

        var max = 0L

        for (i in points.indices) {
            for (j in i + 1..<points.size) {
                val sq = getSquare(points[i], points[j])

                if (sq > max) {
                    max = sq
                }
            }
        }

        return max
    }

    fun getRectangleLinesBy2CornerPoints(pointA: FloorPoint, pointC: FloorPoint): Array<Pair<FloorPoint, FloorPoint>> {
        val pointB = FloorPoint(pointA.first, pointC.second)
        val pointD = FloorPoint(pointC.first, pointA.second)

        return arrayOf(
            Pair(pointA, pointB),
            Pair(pointB, pointC),
            Pair(pointC, pointD),
            Pair(pointD, pointA),
        )
    }

    fun getPossiblePointsPairsQueue(points: List<CompressedPoint>): PriorityQueue<Pair<Pair<CompressedPoint, CompressedPoint>, Long>> {
        val queue = PriorityQueue(compareByDescending<Pair<Pair<CompressedPoint, CompressedPoint>, Long>> { it.second })

        for (i in points.indices) {
            for (j in i + 1..<points.size) {
                val p1 = points[i]
                val p2 = points[j]

                val sq = getSquare(p1.original, p2.original)

                queue.add(Pair(Pair(p1, p2), sq))
            }
        }

        return queue
    }

    fun getAllPointsFromRectangle(lines: Array<Pair<FloorPoint, FloorPoint>>): List<Point> {
        val res = mutableListOf<Point>()

        for (line in lines) {
            val p1 = line.first
            val p2 = line.second

            val iRange = if (p1.first < p2.first) p1.first..p2.first else p2.first..p1.first
            val jRange = if (p1.second < p2.second) p1.second..p2.second else p2.second..p1.second

            for (i in iRange) {
                for (j in jRange) {
                    res += Point(i, j)
                }
            }
        }

        return res
    }

    fun part2(input: List<String>): Long {
        val points = input.map { Point(it.split(",")[0].toInt(), it.split(",")[1].toInt()) }

        var compressedPoints = points.mapIndexed { index, p -> CompressedPoint.from(p, index) }.sortedBy { it.first }

        var currentIndex = -1

        for ((index, point) in compressedPoints.withIndex()) {
            if (currentIndex < 0 || (index > 0 && compressedPoints[index - 1].original.first != compressedPoints[index].original.first)) {
                currentIndex += 1
            }

            point.first = currentIndex
        }

        compressedPoints = compressedPoints.sortedBy { it.second }

        currentIndex = -1

        for ((index, point) in compressedPoints.withIndex()) {
            if (currentIndex < 0 || (index > 0 && compressedPoints[index - 1].original.second != compressedPoints[index].original.second)) {
                currentIndex += 1
            }

            point.second = currentIndex
        }

        compressedPoints = compressedPoints.sortedBy { it.originalOrder }

        val uniqueY = compressedPoints.map { it.first }.toSet().size
        val uniqueX = compressedPoints.map { it.second }.toSet().size

        val matrix = Matrix<FloorPoint?>(Array(uniqueY) { Array(uniqueX) { null }})

        for (point in compressedPoints) {
            matrix.set(point, point.first, point.second)
        }

        var prev = compressedPoints.last()

        for (i in 0..<compressedPoints.size) {
            val current = compressedPoints[i]

            if (prev.first == current.first) {
                val y = prev.first

                val from = (if (current.second <= prev.second) current.second else prev.second) + 1
                val to = (if (current.second > prev.second) current.second else prev.second) - 1

                for (x in from..to) {
                    matrix.set(EdgePoint(x, y), y, x)
                }
            } else {
                val x = prev.second

                val from = (if (current.first <= prev.first) current.first else prev.first) + 1
                val to = (if (current.first > prev.first) current.first else prev.first) - 1

                for (y in from..to) {
                    matrix.set(EdgePoint(x, y), y, x)
                }
            }

            prev = current
        }

        val queue = ArrayDeque<Point>()

        val lastCol = matrix.width() - 1
        for (i in 0..<matrix.height()) {
            if (matrix.get(i, 0) == null) {
                val p = Point(i, 0)
                queue.add(p)
                matrix.set(OutsidePoint(p), p)
            }

            if (matrix.get(i, lastCol) == null) {
                val p = Point(i, lastCol)
                queue.add(p)
                matrix.set(OutsidePoint(p), p)
            }
        }

        val lastRow = matrix.height() - 1
        for (j in 1..<matrix.width() - 1) {
            if (matrix.get(0, j) == null) {
                val p = Point(0, j)
                queue.add(p)
                matrix.set(OutsidePoint(p), p)
            }

            if (matrix.get(lastRow, j) == null) {
                val p = Point(lastRow, j)
                queue.add(p)
                matrix.set(OutsidePoint(p), p)
            }
        }

        var count = 0
        while (queue.isNotEmpty()) {
            count += 1
            val point = queue.removeFirst()

            val neighbours = matrix.getOrthogonalNeighbours(point.first, point.second)

            for (neighbour in neighbours) {
                val element = matrix.get(neighbour)

                if (element != null) {
                    continue
                }

                matrix.set(OutsidePoint(neighbour), neighbour)

                queue.add(neighbour)
            }
        }

        val rectanglesPointsQueue = getPossiblePointsPairsQueue(compressedPoints)

        while (rectanglesPointsQueue.isNotEmpty()) {
            val (rectanglePoints, square) = rectanglesPointsQueue.poll()

            val lines = getRectangleLinesBy2CornerPoints(rectanglePoints.first, rectanglePoints.second)

            val allPoints = getAllPointsFromRectangle(lines)

            if (allPoints.any { matrix.get(it)?.toString() == "." }) {
                continue
            }

            matrix.print(allPoints)

            return square
        }

        return 0L
    }

    runDaySolutions(day, ::part1, ::part2,  setOf())
}
