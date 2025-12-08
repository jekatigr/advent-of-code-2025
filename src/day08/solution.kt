package day08

import utils.Skip
import utils.getIdByXY
import utils.runDaySolutions
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class Box(val id: Int, var circuit: Int, val point: Point3D) {
    fun distanceTo(other: Box): Double {
        return point.distanceTo(other.point)
    }
}

class Point3D(val x: Int, val y: Int, val z: Int) {
    fun distanceTo(other: Point3D): Double {
        return sqrt((x - other.x.toDouble()).pow(2.0) + (y - other.y.toDouble()).pow(2.0) + (z - other.z.toDouble()).pow(2.0))
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun parseBoxes(input: List<String>): List<Box> {
        val boxes = mutableListOf<Box>()

        for ((index, line) in input.withIndex()) {
            val arr = line.split(",")
            val point = Point3D(arr[0].toInt(), arr[1].toInt(), arr[2].toInt())

            boxes += Box(index, index, point)
        }

        return boxes
    }

    fun mergeClosest(boxes: List<Box>, connected: MutableSet<String>): Pair<Box, Box>? {
        var closestA: Box? = null
        var closestB: Box? = null
        var distance = Double.MAX_VALUE

        for (i in boxes.indices) {
            for (j in i+1..boxes.lastIndex) {
                if (connected.contains(getIdByXY(boxes[i].id, boxes[j].id))) {
                    continue
                }

                val dis = boxes[i].distanceTo(boxes[j])

                if (dis < distance) {
                    closestA = boxes[i]
                    closestB = boxes[j]
                    distance = dis
                }
            }
        }

        if (closestA == null || closestB == null) {
            return null
        }

        connected += getIdByXY(closestA.id, closestB.id)
        connected += getIdByXY(closestB.id, closestA.id)

        if (closestA.circuit == closestB.circuit) {
            return null
        }

        val target = closestB.circuit

        for (box in boxes) {
            if (box.circuit == target) {
                box.circuit = closestA.circuit
            }
        }

        return Pair(closestA, closestB)
    }

    fun part1(input: List<String>): Int {
        var conjunctions = input[0].toInt()
        val boxes = parseBoxes(input.takeLast(input.size - 1))

        val connected = mutableSetOf<String>()

        while (conjunctions > 0) {
            mergeClosest(boxes, connected)

            conjunctions -= 1
        }

        val circuits = boxes.groupingBy { it.circuit }.eachCount()

        val sorted = circuits.values.sortedBy { it }.reversed()

        var result = 1

        for (i in 0..min(2, sorted.size - 1)) {
            result *= sorted[i]
        }

        return result
    }

    fun part2(input: List<String>): Long {
        val boxes = parseBoxes(input.takeLast(input.size - 1))

        val connected = mutableSetOf<String>()
        var connectedPair: Pair<Box, Box>? = null

        var count = 0

        while (boxes.groupingBy { it.circuit }.eachCount().keys.size != 1) {
            connectedPair = mergeClosest(boxes, connected)

            count += 1

            if (count % 1000 == 0) {
                println("circuits: ${boxes.groupingBy { it.circuit }.eachCount().keys.size}")
            }
        }

        return connectedPair!!.first.point.x.toLong() * connectedPair.second.point.x.toLong()
    }

    runDaySolutions(day, ::part1, ::part2,  setOf())
}


