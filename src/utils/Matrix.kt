package utils

class Matrix<T>(private val cells: Array<Array<T>>) {
    /**
     * Checks if coordinates of a cell is in matrix area
     */
    fun isValidCoordinates(i: Int, j: Int): Boolean {
        return !(i < 0 || j < 0 || i >= cells.size || j >= cells[0].size)
    }
    fun isValidCoordinates(point: Point): Boolean {
        return isValidCoordinates(point.first, point.second)
    }

    /**
     * Finds all cells coordinates by cell value (O(m*n))
     */
    fun findAll(value: T): Points {
        return findAll { v, _, _ -> v == value }
    }
    /**
     * Finds all cells coordinates by matcher (O(m*n))
     */
    fun findAll(match: (T, Int, Int) -> Boolean): Points {
        val result = mutableListOf<Point>();

        for ((i, row) in cells.withIndex()) {
            for ((j, v) in row.withIndex()) {
                if (match(v,i, j)) {
                    result.add(Pair(i, j))
                }
            }
        }

        return result
    }

    /**
     * Returns vertical and horizontal neighbors coordinates list where move is possible.
     */
    fun getOrthogonalNeighbours(i: Int, j: Int): Array<Point> {
        val list = arrayOf(
            Side.UP.from(i, j),
            Side.DOWN.from(i, j),
            Side.LEFT.from(i, j),
            Side.RIGHT.from(i, j),
        )

        return list.filter { isValidCoordinates(it.first, it.second) }.toTypedArray()
    }

    /**
     * Returns diagonal neighbors coordinates list where move is possible.
     */
    fun getDiagonalDirections(i: Int, j: Int): Array<Point> {
        val list = arrayOf(
            Side.UP_LEFT.from(i, j),
            Side.UP_RIGHT.from(i, j),
            Side.DOWN_LEFT.from(i, j),
            Side.DOWN_RIGHT.from(i, j),
        )

        return list.filter { isValidCoordinates(it.first, it.second) }.toTypedArray()
    }

    /**
     * Returns neighbors coordinates list where move is possible.
     */
    fun getNeighbours(i: Int, j: Int): Array<Point> {
        return getOrthogonalNeighbours(i, j) + getDiagonalDirections(i, j)
    }
    fun getNeighbours(point: Point) = getNeighbours(point.first, point.second)

    fun height() = cells.size
    fun heightRange() = IntRange(0, cells.size - 1)
    fun width() = cells[0].size
    fun widthRange() = IntRange(0, cells[0].size - 1)

    /**
     * Get cell value by coordinates.
     */
    fun get(i: Int, j: Int): T {
        return cells[i][j]
    }
    /**
     * Get cell value by coordinates.
     */
    fun get(point: Point): T {
        return cells[point.first][point.second]
    }

    /**
     * Set cell value by coordinates.
     */
    fun set(value: T, point: Point) {
        cells[point.first][point.second] = value
    }

    /**
     * Pretty prints matrix with some visited nodes marked. Visited set is a set with id's.
     */
    fun print(visited: Set<String>?, separator: String? = "") {
        val blackColor = "\u001b[30m"
        val reset = "\u001b[0m"
        val brightGreenBg = "\u001b[102m"

        for ((index, line) in cells.withIndex()) {
            for ((j, c) in line.withIndex()) {
                val id = getIdByXY(index, j)

                if (visited?.contains(id) == true ) {
                    kotlin.io.print("$brightGreenBg$blackColor$c$reset$separator")
                } else {
                    kotlin.io.print("$c$separator")
                }
            }
            println()
        }
        println()
    }
    fun print(visited: Points, separator: String? = "") {
        print(visited.map { getIdByXY(it.first, it.second) }.toSet(), separator)
    }
    fun print(separator: String? = "") {
        print(null, separator)
    }

    companion object {
        inline fun <reified T>from(input: List<String>, default: T, convertCellValue: (char: Char) -> T): Matrix<T> {
            val rows = input.size
            val cols = input[0].length

            val cells: Array<Array<T>> = Array(rows) { Array(cols) { default } }

            for ((i, line) in input.withIndex()) {
                for ((j, char) in line.toCharArray().withIndex()) {
                    cells[i][j] = convertCellValue(char)
                }
            }

            return Matrix(cells)
        }
    }
}