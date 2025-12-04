package utils

enum class Side {
    UP,
    DOWN,
    LEFT,
    RIGHT,

    UP_LEFT,
    UP_RIGHT,
    DOWN_LEFT,
    DOWN_RIGHT,
    ;

    fun opposite(): Side {
        return when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT

            UP_LEFT -> DOWN_RIGHT
            UP_RIGHT -> DOWN_LEFT
            DOWN_LEFT -> UP_RIGHT
            DOWN_RIGHT -> UP_LEFT
        }
    }

    /**
     * Returns next coordinates from the current ones based on direction
     */
    fun from(i: Int, j: Int): Pair<Int, Int> {
        return when (this) {
            UP -> Pair(i - 1, j)
            DOWN -> Pair(i + 1, j)
            LEFT -> Pair(i, j - 1)
            RIGHT -> Pair(i, j + 1)
            UP_LEFT -> Pair(i - 1, j - 1)
            UP_RIGHT -> Pair(i - 1, j + 1)
            DOWN_LEFT -> Pair(i + 1, j - 1)
            DOWN_RIGHT -> Pair(i + 1, j + 1)
        }
    }

    companion object {
        fun from(char: Char): Side {
            when (char) {
                '^' -> return UP
                '>' -> return RIGHT
                '<' -> return LEFT
                'v' -> return DOWN
            }

            throw Error("Invalid char '$char'")
        }
    }
}
