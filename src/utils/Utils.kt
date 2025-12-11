package utils

typealias Point = Pair<Int, Int>
typealias Points = List<Point>

/**
 * Creates String id by coordinates
 */
fun <T> getIdByXY(y: T, x: T) = "${y}-${x}"
