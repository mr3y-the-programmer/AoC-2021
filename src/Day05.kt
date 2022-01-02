import kotlin.math.max
import kotlin.math.min

fun main() {
    // first part
    fun part1(): Int {
        val lines = readInput("Day05_input")
            .trim()
            .map { it.toGeometricalLine() }
            .filter { it.x1 == it.x2 || it.y1 == it.y2 }
        val (rowsCount, columnsCount) = run {
            max(lines.maxOf { it.y1 }, lines.maxOf { it.y2 }) + 1 to max(lines.maxOf { it.x1 }, lines.maxOf { it.x2 }) + 1
        }
        val board = CoordinatesBoard.getInstance(rowsCount, columnsCount)
        lines.forEach { board.drawLine(it) }
        return board.countOverlappedPoints()
    }

    // second part
    fun part2(): Int {
        val lines = readInput("Day05_input")
            .trim()
            .map { it.toGeometricalLine() }
        val (rowsCount, columnsCount) = run {
            max(lines.maxOf { it.y1 }, lines.maxOf { it.y2 }) + 1 to max(lines.maxOf { it.x1 }, lines.maxOf { it.x2 }) + 1
        }
        val board = CoordinatesBoard.getInstance(rowsCount, columnsCount)
        lines.forEach { board.drawLine(it) }
        return board.countOverlappedPoints()
    }
    println(part1())
    println(part2())
}

class CoordinatesBoard private constructor(val coordinates: Array<Array<Int>>) {

    fun drawLine(line: Line) {
        when {
            line.x1 == line.x2 -> {
                val column = coordinates[line.x1]
                val range = if (line.y1 > line.y2) line.y2..line.y1 else line.y1..line.y2
                for (i in range) {
                    column[i] = column[i] + 1
                }
            }
            line.y1 == line.y2 -> {
                val range = if (line.x1 > line.x2) line.x2..line.x1 else line.x1..line.x2
                for (i in range) {
                    coordinates[i][line.y1] = coordinates[i][line.y1] + 1
                }
            }
            line.slope == 1 -> {
                coordinates[line.x1][line.y1] = coordinates[line.x1][line.y1] + 1
                coordinates[line.x2][line.y2] = coordinates[line.x2][line.y2] + 1
                var nextPointX = min(line.x1, line.x2) + 1
                var nextPointY = min(line.y1, line.y2) + 1
                while (nextPointX < max(line.x1, line.x2) && nextPointY < max(line.y1, line.y2)) {
                    coordinates[nextPointX][nextPointY] = coordinates[nextPointX][nextPointY] + 1
                    nextPointX++
                    nextPointY++
                }
            }
            line.slope == -1 -> {
                coordinates[line.x1][line.y1] = coordinates[line.x1][line.y1] + 1
                coordinates[line.x2][line.y2] = coordinates[line.x2][line.y2] + 1
                if (line.y2 < line.y1) {
                    var nextPointX = min(line.x1, line.x2) + 1
                    var nextPointY = line.y1 - 1
                    while (nextPointX < max(line.x1, line.x2) && nextPointY > line.y2) {
                        coordinates[nextPointX][nextPointY] = coordinates[nextPointX][nextPointY] + 1
                        nextPointX++
                        nextPointY--
                    }
                } else if (line.x2 < line.x1) {
                    var nextPointX = line.x1 - 1
                    var nextPointY = min(line.y1, line.y2) + 1
                    while (nextPointX > line.x2 && nextPointY < max(line.y1, line.y2)) {
                        coordinates[nextPointX][nextPointY] = coordinates[nextPointX][nextPointY] + 1
                        nextPointX--
                        nextPointY++
                    }
                }
            }
        }
    }

    fun countOverlappedPoints(): Int {
        var count = 0
        coordinates.forEach { count += it.filter { it >= 2 }.count() }
        return count
    }

    companion object {
        fun getInstance(rowsCount: Int, columnsCount: Int) = CoordinatesBoard(Array(columnsCount) { Array(rowsCount) { 0 } })
    }
}

private fun String.toGeometricalLine(): Line {
    val (start, end) =
        substringBefore("->").trim().split(',').map { it.toInt() } to substringAfter("->").trim().split(',').map { it.toInt() }
    return Line(start[0], start[1], end[0], end[1])
}

data class Line(
    val x1: Int,
    val y1: Int,
    val x2: Int,
    val y2: Int
) {
    override fun toString(): String {
        return "($x1,$y1) -> ($x2,$y2)"
    }
}

val Line.slope
    get() = (y2 - y1) / (x2 - x1)

