package utils

class Matrix<T>(private val cells: Array<Array<T>>) {
    /**
     * Checks if coordinates of a cell is in matrix area
     */
    fun isValidCoordinates(i: Int, j: Int): Boolean {
        return !(i < 0 || j < 0 || i >= cells.size || j >= cells[0].size)
    }
    fun isValidCoordinates(point: Pair<Int, Int>): Boolean {
        return isValidCoordinates(point.first, point.second)
    }

    /**
     * Finds all cells coordinates by cell value (O(m*n))
     */
    fun findAll(value: T): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>();

        for ((i, row) in cells.withIndex()) {
            for ((j, v) in row.withIndex()) {
                if (value == v) {
                    result.add(Pair(i, j))
                }
            }
        }

        return result
    }

    /**
     * Returns vertical and horizontal neighbors coordinates list where move is possible.
     */
    fun getDirections(i: Int, j: Int): Array<Pair<Int, Int>> {
        val list = arrayOf(
            Side.UP.from(i, j),
            Side.DOWN.from(i, j),
            Side.LEFT.from(i, j),
            Side.RIGHT.from(i, j),
        )

        return list.filter { isValidCoordinates(it.second, it.first) }.toTypedArray()
    }

    /**
     * Get cell value by coordinates.
     */
    fun get(i: Int, j: Int): T {
        return cells[i][j]
    }

    fun print() {
        for (row in cells) {
            println(row.joinToString(" "))
        }
        println()
    }

    /**
     * Pretty prints matrix with some visited nodes marked. Visited set is a set with id's.
     */
    fun print(visited: Set<String>) {
        val blackColor = "\u001b[30m"
        val reset = "\u001b[0m"
        val brightGreenBg = "\u001b[102m"

        for ((index, line) in cells.withIndex()) {
            for ((j, c) in line.withIndex()) {
                val id = getIdByXY(index, j)

                if (id in visited) {
                    print("$brightGreenBg$blackColor$c$reset ")
                } else {
                    print("$c ")
                }
            }
            println()
        }
        println()
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