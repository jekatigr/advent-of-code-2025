package day11

import utils.Skip
import utils.runDaySolutions
import kotlin.collections.set

class Graph(val nodes: Map<String, List<String>>) {
    fun findWaysNumber(from: String, to: String, visited: MutableSet<String>): Long {
        if (from == to) {
            return 1
        }

        if (nodes[from] == null) {
            return 0L
        }

        visited.add(from)

        var res = 0L
        for (node in nodes[from]!!) {
            if (visited.contains(node)) {
                continue
            }

            res += findWaysNumber(node, to, visited)
        }

        visited.remove(from)

        return res
    }

    fun findWaysWithIncludedDACAndFFT(from: String, to: String): Long {
        val reverseNodes = mutableMapOf<String, MutableList<String>>()

        for (node in nodes) {
            for (n in node.value) {
                if (reverseNodes[n] == null) {
                    reverseNodes[n] = mutableListOf()
                }

                reverseNodes[n]!!.add(node.key)
            }
        }

        val reversedGraph = Graph(reverseNodes)

        val dp = mutableMapOf<String, Array<Long>>()

        dp[from] = Array(4) { 0L }

        dp[from]?.set(0, 1) // number of ways to node "from" with zero number of dac and fft
        dp[from]?.set(1, 0) // at least 1 dac and zero fft
        dp[from]?.set(2, 0) // at least 1 fft and zero dac
        dp[from]?.set(3, 0) // at least 1 dac and 1 fft

        val result = resolve(reversedGraph, dp, to)

        return result
    }

    private fun resolve(reversedGraph: Graph, dp: MutableMap<String, Array<Long>>, target: String): Long {
        if (dp[target] != null) {
            return dp[target]!![3]
        }

        if (reversedGraph.nodes[target] == null || reversedGraph.nodes[target]!!.isEmpty()) {
            return 0L
        }

        dp[target] = Array(4) { 0L }

        dp[target]!![0] = 0
        dp[target]!![1] = 0
        dp[target]!![2] = 0
        dp[target]!![3] = 0

        val isDAC = target == "dac"
        val isFFT = target == "fft"

        for (node in reversedGraph.nodes[target]!!) {
            if (dp[node] == null) {
                resolve(reversedGraph, dp, node)
            }

            if (isDAC) {
                dp[target]?.set(1, dp[node]!![0] + dp[node]!![1] + dp[target]!![1])
                dp[target]?.set(3, dp[node]!![2] + dp[node]!![3] + dp[target]!![3])

                continue
            }

            if (isFFT) {
                dp[target]?.set(2, dp[node]!![0] + dp[node]!![2] + dp[target]!![2])
                dp[target]?.set(3, dp[node]!![1] + dp[node]!![3] + dp[target]!![3])

                continue
            }

            dp[target]?.set(0, dp[node]!![0] + dp[target]!![0])
            dp[target]?.set(1, dp[node]!![1] + dp[target]!![1])
            dp[target]?.set(2, dp[node]!![2] + dp[target]!![2])
            dp[target]?.set(3, dp[node]!![3] + dp[target]!![3])
        }

        return dp[target]!![3]
    }

    companion object {
        fun from(lines: List<String>): Graph {
            val map = mutableMapOf<String, MutableList<String>>()

            for (line in lines) {
                val arr = line.split(": ")

                map[arr[0]] = arr[1].split(' ').toMutableList()
            }

            return Graph(map)
        }
    }
}

fun main() {
    val day = (object {}).javaClass.packageName.takeLast(2).toInt()

    fun part1(input: List<String>): Long {
        val graph = Graph.from(input)

        return graph.findWaysNumber("you", "out", mutableSetOf())
    }

    fun part2(input: List<String>): Long {
        val graph = Graph.from(input)

        return graph.findWaysWithIncludedDACAndFFT("svr", "out")
    }

    runDaySolutions(day, ::part1, ::part2,  setOf())
}
