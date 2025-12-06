package utils

import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("./src/$name.txt")
    .readText()
    .removeSuffix("\r\n")
    .removeSuffix("\n")
    .lines()

/**
 * Run solution for test-case
 */
inline fun <reified T>testSolution(part: Int, testCaseNumber: Int, solution: (input: List<String>) -> T, testCase: TestCase<T>): Boolean {
    val solutionTestResult = solution(testCase.input)

    if (solutionTestResult == testCase.getTypedExpected<T>()) {
        return true
    }

    val redColor = "\u001b[31m"
    val reset = "\u001b[0m"

    println("${redColor}Part $part, testcase number $testCaseNumber:$reset returned value is $solutionTestResult instead of ${testCase.expectedString}")

    return false
}

const val UNIT_TESTS_DIVIDER = "==="
const val EXPECTED_PART1_PREFIX = "1:"
const val EXPECTED_PART2_PREFIX = "2:"

class TestCase<T>(var input: List<String>, val expectedString: String) {
    inline fun <reified T> getTypedExpected(): T {
        return when(T::class) {
            Long::class -> expectedString.toLong()
            Int::class -> expectedString.toInt()
            String::class -> expectedString
            else -> error("Converter unavailable for ${T::class}")
        } as T
    }
}
class TestCases<P1, P2> {
    val part1 = mutableListOf<TestCase<P1>>()
    val part2 = mutableListOf<TestCase<P2>>()

    fun addToPart1(testCase: TestCase<P1>) {
        part1.add(testCase)
    }

    fun addToPart2(testCase: TestCase<P2>) {
        part2.add(testCase)
    }
}
/**
 * Function will split testcases from a raw input
 */
fun <P1, P2>getTestCases(testInput: List<String>): TestCases<P1, P2> {
    val testCases = TestCases<P1, P2>()

    var expectedPt1: String? = null
    var expectedPt2: String? = null
    var testLines = mutableListOf<String>()

    for (line in testInput) {
        if (line.startsWith(EXPECTED_PART1_PREFIX) || line.startsWith(EXPECTED_PART2_PREFIX)) {
            if (line.startsWith(EXPECTED_PART1_PREFIX)) {
                expectedPt1 = line.removePrefix(EXPECTED_PART1_PREFIX).trim()
            }
            if (line.startsWith(EXPECTED_PART2_PREFIX)) {
                expectedPt2 = line.removePrefix(EXPECTED_PART2_PREFIX).trim()
            }

            continue
        }

        if (line == UNIT_TESTS_DIVIDER) {
            if (expectedPt1 != null) {
                testCases.addToPart1(TestCase(testLines, expectedPt1))
            }
            if (expectedPt2 != null) {
                testCases.addToPart2(TestCase(testLines, expectedPt2))
            }
            expectedPt1 = null
            expectedPt2 = null
            testLines = mutableListOf()

            continue
        }

        testLines.add(line)
    }

    if (expectedPt1 != null) {
        testCases.addToPart1(TestCase(testLines, expectedPt1))
    }
    if (expectedPt2 != null) {
        testCases.addToPart2(TestCase(testLines, expectedPt2))
    }

    return testCases
}

/**
 * Run all test cases for test input
 */
inline fun <reified T>testSolutions(part: Int, solution: (input: List<String>) -> T, testCases: List<TestCase<T>>): Int {
    var passed = 0

    for ((index, testCase) in testCases.withIndex()) {
        if (testSolution(part, index + 1, solution, testCase)) {
            passed += 1
        }
    }

    return passed
}

enum class Skip {
    PART1_TESTS,
    PART1_SOLUTION,
    PART2_TESTS,
    PART2_SOLUTION
}
/**
 * Run all tests and solutions
 */
inline fun <reified P1, reified P2>runDaySolutions(day: Int, solutionPart1: (input: List<String>) -> P1, solutionPart2: (input: List<String>) -> P2, skip: Set<Skip> = setOf()) {
    val dayStr = day.toString().padStart(2, '0')

    val brightGreenBg = "\u001b[102m"
    val grayBg = "\u001b[100m"
    val blackColor = "\u001b[30m"
    val greenColor = "\u001b[32m"
    val grayColor = "\u001b[90m"
    val reset = "\u001b[0m"

    println()
    println("$brightGreenBg$blackColor======= AoC 2025: Day $day =======$reset")
    println()

    val testInput = readInput("day$dayStr/tests")
    val input = readInput("day$dayStr/main")

    val testCases = getTestCases<P1, P2>(testInput)

    println("$grayBg------- Part 1 -------$reset")
    var runMainPart1 = true
    if (Skip.PART1_TESTS !in skip) {
        val passed = testSolutions(1, solutionPart1, testCases.part1)
        if (passed == testCases.part1.size) {
            println("$greenColor${testCases.part1.size} testcase(s) passed$reset")
        } else {
            runMainPart1 = false
            println("$passed (out of ${testCases.part1.size}) testcase(s) passed")
        }
    } else {
        println("$grayColor${testCases.part1.size} testcase(s) skipped$reset")
    }

    if (Skip.PART1_SOLUTION !in skip && runMainPart1) {
        val part1Result = solutionPart1(input)
        println("=> Main answer: $greenColor$part1Result$reset")
    } else {
        println("${grayColor}Part 1 main input run skipped$reset")
    }

    println()
    println("$grayBg------- Part 2 -------$reset")
    var runMainPart2 = true
    if (Skip.PART2_TESTS !in skip) {
        val passed = testSolutions(2, solutionPart2, testCases.part2)
        if (passed == testCases.part2.size) {
            println("$greenColor${testCases.part2.size} testcase(s) passed$reset")
        } else {
            runMainPart2 = false
            println("$passed (out of ${testCases.part2.size}) testcase(s) passed")
        }
    } else {
        println("$grayColor${testCases.part2.size} testcase(s) skipped$reset")
    }

    if (Skip.PART2_SOLUTION !in skip && runMainPart2) {
        val part2Result = solutionPart2(input)
        println("=> Main answer: $greenColor$part2Result$reset")
    } else {
        println("${grayColor}Part 2 main input run skipped$reset")
    }

    println()
    println("$brightGreenBg$blackColor------- All done -------$reset")
}
