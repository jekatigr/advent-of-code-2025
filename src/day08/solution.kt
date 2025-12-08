package day08

import utils.runDaySolutions
import java.util.PriorityQueue
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class Box(val point: Point3D) {
    var circuitParent = this
        get() {
            while (field != this) {
                return field.circuitParent
            }

            return field
        }

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

        for (line in input) {
            val arr = line.split(",")
            val point = Point3D(arr[0].toInt(), arr[1].toInt(), arr[2].toInt())

            boxes += Box(point)
        }

        return boxes
    }

    fun createConnectionQueue(boxes: List<Box>): PriorityQueue<Triple<Box, Box, Double>> {
        val queue = PriorityQueue(compareBy<Triple<Box, Box, Double>> { it.third })

        for (i in boxes.indices) {
            for (j in i + 1..boxes.lastIndex) {
                queue.add(Triple(boxes[i], boxes[j], boxes[i].distanceTo(boxes[j])))
            }
        }

        return queue
    }

    fun mergeClosest(closestQueue: PriorityQueue<Triple<Box, Box, Double>>): Pair<Box, Box>? {
        val (closestA, closestB) = closestQueue.poll()
        val parentA = closestA.circuitParent
        val parentB = closestB.circuitParent

        if (parentA == parentB) {
            return null
        }

        parentB.circuitParent = parentA

        return Pair(closestA, closestB)
    }

    fun part1(input: List<String>): Int {
        var conjunctions = input[0].toInt()
        val boxes = parseBoxes(input.takeLast(input.size - 1))
        val queue = createConnectionQueue(boxes)

        while (conjunctions > 0) {
            mergeClosest(queue)

            conjunctions -= 1
        }

        val circuits = mutableMapOf<Box, Int>()

        for (box in boxes) {
            val parent = box.circuitParent

            circuits[parent] = (circuits[parent] ?: 0) + 1
        }

        val sorted = circuits.values.sortedBy { it }.reversed()

        var result = 1

        for (i in 0..min(2, sorted.size - 1)) {
            result *= sorted[i]
        }

        return result
    }

    fun part2(input: List<String>): Long {
        val boxes = parseBoxes(input.takeLast(input.size - 1))
        var connectedPair: Pair<Box, Box>? = null
        val queue = createConnectionQueue(boxes)

        do {
            val circuits = mutableMapOf<Box, Int>()

            for (box in boxes) {
                val parent = box.circuitParent

                circuits[parent] = (circuits[parent] ?: 0) + 1
            }

            connectedPair = mergeClosest(queue) ?: connectedPair
        } while (circuits.keys.size > 1)

        return connectedPair!!.first.point.x.toLong() * connectedPair.second.point.x.toLong()
    }

    runDaySolutions(day, ::part1, ::part2,  setOf())
}


